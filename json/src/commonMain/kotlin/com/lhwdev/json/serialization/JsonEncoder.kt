@file:OptIn(ExperimentalSerializationApi::class)

package com.lhwdev.json.serialization

import com.lhwdev.json.JsonAnyWriter
import com.lhwdev.json.JsonArrayWriter
import com.lhwdev.json.JsonObjectWriter
import com.lhwdev.json.JsonWriter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder


abstract class JsonEncoder(val adapter: JsonAdapter) : AbstractEncoder() {
	abstract val writer: JsonWriter
	
	override val serializersModule = adapter.serializersModule
	
	private fun encodeJsonObjectMap(descriptor: SerialDescriptor) = adapter.config.mapAsJsonObject.isEnabled &&
		descriptor.getElementDescriptor(0).kind == PrimitiveKind.STRING
	
	override fun beginStructure(descriptor: SerialDescriptor) = when(descriptor.kind) {
		StructureKind.CLASS, StructureKind.OBJECT -> JsonTreeEncoder(
			adapter,
			writer.beginObject(),
			this
		)
		StructureKind.MAP ->
			if(encodeJsonObjectMap(descriptor)) JsonMapEncoder(adapter, writer.beginObject(), this)
			else JsonListEncoder(adapter, writer.beginArray(), this)
		StructureKind.LIST -> JsonListEncoder(adapter, writer.beginArray(), this)
		is PolymorphicKind -> JsonListEncoder(adapter, writer.beginArray(), this)
		else -> error("nothing")
	}
	
	override fun encodeBoolean(value: Boolean) {
		writer.writeBoolean(value)
	}
	
	override fun encodeByte(value: Byte) {
		writer.writeByte(value)
	}
	
	override fun encodeChar(value: Char) {
		writer.writeString("$value")
	}
	
	override fun encodeDouble(value: Double) {
		writer.writeDouble(value)
	}
	
	override fun encodeFloat(value: Float) {
		writer.writeFloat(value)
	}
	
	override fun encodeInt(value: Int) {
		writer.writeInt(value)
	}
	
	override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
		writer.writeString(enumDescriptor.getElementName(index))
	}
	
	override fun encodeLong(value: Long) {
		writer.writeLong(value)
	}
	
	override fun encodeNull() {
		writer.writeNull()
	}
	
	override fun encodeShort(value: Short) {
		writer.writeShort(value)
	}
	
	override fun encodeString(value: String) {
		writer.writeString(value)
	}
}


class JsonTreeEncoder(adapter: JsonAdapter, override val writer: JsonObjectWriter, val parent: JsonEncoder?) :
	JsonEncoder(adapter) {
	override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
		val key = descriptor.getElementName(index)
		writer.writeKey(key)
		return true
	}
	
	override fun endStructure(descriptor: SerialDescriptor) {
		super.endStructure(descriptor)
		if(parent == null) writer.end()
		else parent.writer.endObject(writer)
	}
}

class JsonMapEncoder(adapter: JsonAdapter, override val writer: JsonObjectWriter, val parent: JsonEncoder?) :
	JsonEncoder(adapter) {
	private var isKey = false
	
	override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
		isKey = index % 2 == 0
		return true
	}
	
	override fun encodeString(value: String) {
		if(isKey) writer.writeKey(value)
		else super.encodeString(value)
	}
	
	override fun endStructure(descriptor: SerialDescriptor) {
		super.endStructure(descriptor)
		if(parent == null) writer.end()
		else parent.writer.endObject(writer)
	}
}

class JsonListEncoder(adapter: JsonAdapter, override val writer: JsonArrayWriter, val parent: JsonEncoder?) :
	JsonEncoder(adapter) {
	override fun endStructure(descriptor: SerialDescriptor) {
		if(parent == null) writer.end()
		else parent.writer.endArray(writer)
	}
}

class JsonAnyEncoder(adapter: JsonAdapter, override val writer: JsonAnyWriter) : JsonEncoder(adapter)
