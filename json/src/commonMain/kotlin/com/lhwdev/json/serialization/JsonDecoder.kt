@file:OptIn(ExperimentalSerializationApi::class)

package com.lhwdev.json.serialization

import com.lhwdev.json.JsonAnyParser
import com.lhwdev.json.JsonArrayParser
import com.lhwdev.json.JsonObjectParser
import com.lhwdev.json.JsonParser
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
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
	
	override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
		when(descriptor.kind as StructureKind) {
			StructureKind.CLASS, StructureKind.MAP, StructureKind.OBJECT -> JsonMapDecoder(
				adapter,
				parser.beginObject(),
				this
			)
			StructureKind.LIST -> JsonListDecoder(adapter, parser.beginArray(), this)
			is PolymorphicKind -> JsonListDecoder(adapter, parser.beginArray(), this)
		}
	
	// no parent in beginStructurePointed: intended
	fun beginStructurePointed(descriptor: SerialDescriptor): CompositeDecoder = when(descriptor.kind as StructureKind) {
		StructureKind.CLASS, StructureKind.MAP, StructureKind.OBJECT -> JsonMapDecoder(
			adapter,
			parser.readObjectPointed(),
			null
		)
		StructureKind.LIST -> JsonListDecoder(adapter, parser.readArrayPointed(), null)
		is PolymorphicKind -> JsonListDecoder(adapter, parser.readArrayPointed(), null)
	}
}


class JsonMapDecoder(adapter: JsonAdapter, override val parser: JsonObjectParser, val parent: JsonDecoder?) :
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

class JsonListDecoder(adapter: JsonAdapter, override val parser: JsonArrayParser, val parent: JsonDecoder?) :
	JsonDecoder(adapter) {
	var currentIndex = 0
		private set
	
	override fun decodeElementIndex(descriptor: SerialDescriptor) =
		if(parser.isEnd()) CompositeDecoder.DECODE_DONE else currentIndex++
	
	override fun endStructure(descriptor: SerialDescriptor) {
		super.endStructure(descriptor)
		if(parent == null) parser.end()
		else parent.parser.endArray(parser)
	}
}

class JsonRootDecoder(adapter: JsonAdapter, override val parser: JsonAnyParser) : JsonDecoder(adapter) {
	override fun decodeElementIndex(descriptor: SerialDescriptor) =
		error("cannot decode element index with this type of decoder")
}
