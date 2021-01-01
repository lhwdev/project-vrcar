@file:OptIn(ExperimentalSerializationApi::class)

package com.lhwdev.json.serialization

import com.lhwdev.json.JsonAnyParser
import com.lhwdev.json.JsonArrayParser
import com.lhwdev.json.JsonObjectParser
import com.lhwdev.json.JsonParser
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder


abstract class JsonDecoder(val adapter: JsonAdapter) : AbstractDecoder() {
	abstract val parser: JsonParser
	
	override val serializersModule = adapter.serializersModule
	
	override fun decodeBoolean() = parser.readBoolean()
	
	override fun decodeByte() = parser.readByte()
	override fun decodeShort() = parser.readShort()
	override fun decodeInt() = parser.readInt()
	override fun decodeLong() = parser.readLong()
	
	override fun decodeChar() = parser.readString().single()
	
	override fun decodeFloat() = parser.readFloat()
	override fun decodeDouble() = parser.readDouble()
	
	override fun decodeString() = parser.readString()
	override fun decodeEnum(enumDescriptor: SerialDescriptor) =
		enumDescriptor.getElementIndexOrThrow(parser.readString())
	
	override fun decodeNotNullMark() = !parser.peekIsNull()
	override fun decodeNull(): Nothing? {
		parser.readNull()
		return null
	}
	
	private fun decodeJsonObjectMap(descriptor: SerialDescriptor): Boolean {
		val structureToken = parser.tokenizer.next()
		parser.tokenizer.pushPeek(structureToken)
		val isAllowed = adapter.config.mapAsJsonObject.isAllowed
		val canDecodeAsMap = descriptor.getElementDescriptor(0).kind == PrimitiveKind.STRING
		val alreadyJsonMap = structureToken.text == "{"
		
		return if(isAllowed && canDecodeAsMap) alreadyJsonMap
		else {
			require(!canDecodeAsMap) {
				when {
					!canDecodeAsMap -> "read json object but the key type of Map is not string: ${
						descriptor.getElementDescriptor(0).serialName
					}"
					else -> "read json object for map but configuration did not allow; enable JsonConfig.jsonMapWithStringKey"
				}
			}
			false
		}
	}
	
	override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
		when(descriptor.kind as StructureKind) {
			StructureKind.CLASS, StructureKind.OBJECT -> JsonTreeDecoder(adapter, parser.beginObject(), this)
			StructureKind.MAP -> if(decodeJsonObjectMap(descriptor))
				JsonMapDecoder(adapter, parser.beginObject(), this)
			else JsonListDecoder(adapter, parser.beginArray(), this)
			StructureKind.LIST -> JsonListDecoder(adapter, parser.beginArray(), this)
			is PolymorphicKind -> JsonListDecoder(adapter, parser.beginArray(), this)
		}
	
	// orphan parent in beginStructurePointed: intended
	fun beginStructurePointed(descriptor: SerialDescriptor): CompositeDecoder = when(descriptor.kind as StructureKind) {
		StructureKind.CLASS, StructureKind.OBJECT -> JsonTreeDecoder(adapter, parser.readObjectPointed(), null)
		StructureKind.MAP -> if(decodeJsonObjectMap(descriptor))
			JsonMapDecoder(adapter, parser.readObjectPointed(), null)
		else JsonListDecoder(adapter, parser.readArrayPointed(), null)
		StructureKind.LIST -> JsonListDecoder(adapter, parser.readArrayPointed(), null)
		is PolymorphicKind -> JsonListDecoder(adapter, parser.readArrayPointed(), null)
	}
}


class JsonTreeDecoder(adapter: JsonAdapter, override val parser: JsonObjectParser, val parent: JsonDecoder?) :
	JsonDecoder(adapter) {
	override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
		val key = parser.readKey()
		return if(key == null) CompositeDecoder.DECODE_DONE else descriptor.getElementIndex(key)
	}
	
	override fun endStructure(descriptor: SerialDescriptor) {
		super.endStructure(descriptor)
		if(parent == null) parser.end()
		else parent.parser.endObject(parser)
	}
}

// the structure of map is like [key1, value1, key2, value2, ...]
// calling this requires the type of key to be string.
class JsonMapDecoder(adapter: JsonAdapter, override val parser: JsonObjectParser, val parent: JsonDecoder?) :
	JsonDecoder(adapter) {
	var currentIndex = 0
		private set
	var lastKey: String? = null
	
	override fun decodeCollectionSize(descriptor: SerialDescriptor) = parser.childrenCount
	
	override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
		if(currentIndex % 2 == 0) {
			if(lastKey != null) error("Illegal decoding detected: lastKey=$lastKey")
			val key = parser.readKey() ?: return CompositeDecoder.DECODE_DONE
			lastKey = key
		}
		
		return currentIndex++
	}
	
	override fun decodeString(): String {
		val last = lastKey
		if(last != null) {
			lastKey = null
			return last
		}
		return super.decodeString()
	}
	
	override fun endStructure(descriptor: SerialDescriptor) {
		super.endStructure(descriptor)
		if(parent == null) parser.end()
		else parent.parser.endObject(parser)
	}
}

class JsonListDecoder(adapter: JsonAdapter, override val parser: JsonArrayParser, val parent: JsonDecoder?) :
	JsonDecoder(adapter) {
	var currentIndex = 0
		private set
	
	override fun decodeCollectionSize(descriptor: SerialDescriptor) = parser.childrenCount
	
	override fun decodeElementIndex(descriptor: SerialDescriptor) =
		if(parser.isEnd()) CompositeDecoder.DECODE_DONE else currentIndex++
	
	override fun endStructure(descriptor: SerialDescriptor) {
		super.endStructure(descriptor)
		if(parent == null) parser.end()
		else parent.parser.endArray(parser)
	}
}

class JsonAnyDecoder(adapter: JsonAdapter, override val parser: JsonAnyParser) : JsonDecoder(adapter) {
	override fun decodeElementIndex(descriptor: SerialDescriptor) =
		error("cannot decode element index with this type of decoder")
}
