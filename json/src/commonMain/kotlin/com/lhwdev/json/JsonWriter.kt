package com.lhwdev.json

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


fun escape(string: String, mode: StringUnicodeEncodeMode) = buildString(capacity = (string.length * 1.2f).toInt()) {
	fun Char.toUnicode() = "\\u${toInt().toString(16).padStart(4, '0')}"
	
	for(c in string) append(
		when(c) {
			'\"' -> "\\\""
			'\\' -> "\\\\"
			'\b' -> "\\b"
			'\u000C' -> "\\f"
			'\n' -> "\\n"
			'\r' -> "\\r"
			'\t' -> "\\t"
			else -> {
				when(mode) {
					StringUnicodeEncodeMode.asIs -> append(c)
					StringUnicodeEncodeMode.allInAscii -> append(if(c <= 127.toChar()) c else c.toUnicode())
					StringUnicodeEncodeMode.always -> append(c.toUnicode())
				}
				continue
			}
		}
	)
}


abstract class JsonWriter(val config: JsonConfig, val output: CharOutput, val depth: Int) {
	protected open fun beginWriteValue() {}
	protected open fun endWriteValue() {}
	
	protected fun writeln(indents: Int = depth) {
		val prettyPrint = config.prettyPrint ?: return
		output.write('\n')
		output.write(prettyPrint.indent.repeat(indents))
	}
	
	protected fun writeValue(value: String) {
		beginWriteValue()
		output.write(value)
		endWriteValue()
	}
	
	fun writeNull() {
		writeValue("null")
	}
	
	fun writeByte(value: Byte) {
		writeValue("$value")
	}
	
	fun writeShort(value: Short) {
		writeValue("$value")
	}
	
	fun writeInt(value: Int) {
		writeValue("$value")
	}
	
	fun writeLong(value: Long) {
		writeValue("$value")
	}
	
	fun writeDouble(value: Double) {
		writeValue("$value")
	}
	
	fun writeFloat(value: Float) {
		writeValue("$value")
	}
	
	fun writeBoolean(value: Boolean) {
		writeValue("$value")
	}
	
	fun writeString(value: String) {
		if(config.noQuote.isEnabled && config.writeValueWithoutQuote && isLenientAvailable(value)) writeValue(value)
		else writeValue("\"${escape(value, config.keyStringUnicodeEncodeMode)}\"")
	}
	
	fun beginObject(): JsonObjectWriter {
		beginWriteValue()
		return JsonObjectWriter(config, output, depth + 1)
	}
	
	fun endObject(writer: JsonObjectWriter) {
		writer.end()
		endWriteValue()
	}
	
	@OptIn(ExperimentalContracts::class)
	inline fun writeObject(block: JsonObjectWriter.() -> Unit) {
		contract {
			callsInPlace(block, InvocationKind.EXACTLY_ONCE)
		}
		beginObject().apply {
			block()
			endObject(this)
		}
	}
	
	fun beginArray(): JsonArrayWriter {
		beginWriteValue()
		return JsonArrayWriter(config, output, depth + 1)
	}
	
	fun endArray(writer: JsonArrayWriter) {
		writer.end()
		endWriteValue()
	}
	
	@OptIn(ExperimentalContracts::class)
	inline fun writeArray(block: JsonArrayWriter.() -> Unit) {
		contract {
			callsInPlace(block, InvocationKind.EXACTLY_ONCE)
		}
		beginArray().apply {
			block()
			endArray(this)
		}
	}
	
	internal open fun end() {}
}


// it is good to be safe
private val sLenientPattern = Regex("([a-zA-Z_$.])([a-zA-Z0-9_$.])*")
private fun isLenientAvailable(text: String) = sLenientPattern.matches(text)


class JsonObjectWriter(config: JsonConfig, output: CharOutput, depth: Int) : JsonWriter(config, output, depth) {
	init {
		output.write(config.prettyPrint?.objectBegin ?: "{")
	}
	
	private var lastKey = false
	private var isFirst = true
	private var elementCount = 0
	
	fun writeKey(key: String) {
		if(lastKey) error("writeKey was called twice")
		lastKey = true
		
		if(isFirst) isFirst = false
		else output.write(config.prettyPrint?.objectComma ?: ",")
		writeln()
		
		if(config.noQuote.isEnabled && isLenientAvailable(key)) output.write(key)
		else output.write("\"${escape(key, config.keyStringUnicodeEncodeMode)}\"")
		output.write(config.prettyPrint?.objectPair ?: ":")
		
		elementCount++
	}
	
	override fun beginWriteValue() {
		if(!lastKey) error("beginWriteValue was called twice")
		lastKey = false
		super.beginWriteValue()
	}
	
	override fun end() {
		if(lastKey) error("last key has no corresponding value")
		
		super.end()
		if(elementCount != 0) writeln(indents = depth - 1)
		output.write(config.prettyPrint?.objectEnd ?: "}")
	}
}

class JsonArrayWriter(config: JsonConfig, output: CharOutput, depth: Int) : JsonWriter(config, output, depth) {
	init {
		output.write(config.prettyPrint?.arrayBegin ?: "[")
	}
	
	private var isFirst = true
	private var elementCount = 0
	
	override fun beginWriteValue() {
		super.beginWriteValue()
		
		if(isFirst) isFirst = false
		else output.write(config.prettyPrint?.arrayComma ?: ",")
		writeln()
		
		elementCount++
	}
	
	override fun end() {
		super.end()
		if(elementCount != 0) writeln(indents = depth - 1)
		output.write(config.prettyPrint?.arrayEnd ?: "]")
	}
}

class JsonAnyWriter(config: JsonConfig, output: CharOutput) : JsonWriter(config, output, 0)
