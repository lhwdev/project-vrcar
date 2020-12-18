package com.lhwdev.json

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


private fun Iterator<JsonToken>.skipGroup(initialDepth: Int = 0) {
	var depth = initialDepth
	do {
		val token = next()
		if(token.type == JsonToken.Type.structural) when(token.text) {
			"[", "{" -> depth++
			"]", "}" -> depth--
		}
	} while(depth != 0)
}

abstract class JsonParser(val config: JsonConfig, val tokenizer: PeekableCloneableIterator<JsonToken>) {
	@Suppress("NOTHING_TO_INLINE")
	@OptIn(ExperimentalContracts::class)
	protected inline fun <R> readValue(operation: () -> R): R {
		contract {
			callsInPlace(operation, InvocationKind.EXACTLY_ONCE)
		}
		
		beginReadValue()
		val value = operation()
		endReadValue()
		return value
	}
	
	protected open fun beginReadValue() {}
	protected open fun endReadValue() {}
	
	protected fun readValue() = readValue { tokenizer.next() }
	protected fun readValue(type: JsonToken.Type) = readValue().also {
		require(it.type == type)
	}
	
	abstract fun peekIsNull(): Boolean
	fun readNull() = readValue(JsonToken.Type.nullLiteral)
	
	fun readByte() = readValue(JsonToken.Type.numberLiteral).text.toByte()
	fun readShort() = readValue(JsonToken.Type.numberLiteral).text.toShort()
	fun readInt() = readValue(JsonToken.Type.numberLiteral).text.toInt()
	fun readLong() = readValue(JsonToken.Type.numberLiteral).text.toLong()
	fun readDouble() = readValue(JsonToken.Type.numberLiteral).text.toDouble()
	fun readFloat() = readValue(JsonToken.Type.numberLiteral).text.toFloat()
	fun readBoolean() = readValue(JsonToken.Type.booleanLiteral).text.toBoolean()
	fun readString() = readValue(JsonToken.Type.string).text
	
	
	fun skipValue() {
		tokenizer.skipGroup()
	}
	
	protected fun readStructure(text: String) = tokenizer.next(text, JsonToken.Type.structural)
	
	val readPointedSupported get() = tokenizer.cloneSupported
	
	fun beginObject(): JsonObjectParser {
		beginReadValue()
		return JsonObjectParser(config, tokenizer)
	}
	
	fun endObject(parser: JsonObjectParser) {
		parser.end()
		endReadValue()
	}
	
	@OptIn(ExperimentalContracts::class)
	inline fun readObject(block: JsonObjectParser.() -> Unit) {
		contract {
			callsInPlace(block, InvocationKind.EXACTLY_ONCE)
		}
		beginObject().apply {
			block()
			endObject(this)
		}
	}
	
	fun readObjectPointed(): JsonObjectParser {
		val newTokenizer: PeekableCloneableIterator<JsonToken>
		readValue {
			newTokenizer = PeekableCloneableIterator(tokenizer.cloneHere())
			tokenizer.skipGroup()
		}
		return JsonObjectParser(config, newTokenizer)
	}
	
	fun beginArray(): JsonArrayParser {
		beginReadValue()
		return JsonArrayParser(config, tokenizer)
	}
	
	fun endArray(parser: JsonArrayParser) {
		parser.end()
		endReadValue()
	}
	
	@OptIn(ExperimentalContracts::class)
	inline fun readArray(crossinline block: JsonArrayParser.() -> Unit) {
		contract {
			callsInPlace(block, InvocationKind.EXACTLY_ONCE)
		}
		beginArray().apply {
			block()
			endArray(this)
		}
	}
	
	fun readArrayPointed(): JsonArrayParser {
		val newTokenizer: PeekableCloneableIterator<JsonToken>
		readValue {
			newTokenizer =
				PeekableCloneableIterator(JsonTokenizerCloneable(config, """["가","나","다"]""".cloneableIterator()))
			tokenizer.skipGroup()
		}
		return JsonArrayParser(config, newTokenizer)
	}
	
	
	// higher-level interfaces
	// fun readValue()
	
	
	internal open fun end(): Boolean = true
}


class JsonObjectParser(config: JsonConfig, tokenizer: PeekableCloneableIterator<JsonToken>) :
	JsonParser(config, tokenizer) {
	constructor(config: JsonConfig, tokenizer: CloneableIterator<JsonToken>) : this(config, PeekableCloneableIterator(tokenizer))
	
	private var isFirst = true
	private var isFinished = false
	private var lastKey = false
	
	init {
		readStructure("{")
	}
	
	
	override fun peekIsNull(): Boolean {
		val token = tokenizer.next()
		tokenizer.pushPeek(token)
		
		return token.type == JsonToken.Type.nullLiteral
	}
	
	fun readKey(): String? {
		if(isFinished) throw IllegalStateException("already finished")
		if(lastKey) throw IllegalStateException("readKey() was called twice")
		
		val token = tokenizer.next()
		if(token.type == JsonToken.Type.structural && token.text == "}") {
			isFinished = true
			return null
		}
		tokenizer.pushPeek(token)
		
		lastKey = true
		
		if(isFirst) isFirst = false
		else readStructure(",")
		
		val text = tokenizer.next(JsonToken.Type.string).text
		readStructure(":")
		
		return text
	}
	
	override fun beginReadValue() {
		if(!lastKey) throw IllegalStateException("readValue() was called twice")
		lastKey = false
	}
	
	fun skipLastValue(lastToken: JsonToken) {
		combinedIterator(lastToken, tokenizer).skipGroup()
	}
	
	override fun end(): Boolean {
		if(lastKey) error("last key has no corresponding value")
		if(!isFinished) readStructure("}")
		return true
	}
}

class JsonArrayParser(config: JsonConfig, tokenizer: PeekableCloneableIterator<JsonToken>) :
	JsonParser(config, tokenizer) {
	constructor(config: JsonConfig, tokenizer: CloneableIterator<JsonToken>) : this(config, PeekableCloneableIterator(tokenizer))
	
	private var isFirst = true
	
	init {
		readStructure("[")
	}
	
	override fun peekIsNull(): Boolean {
		val token = tokenizer.next()
		
		// we pulled ',' and '?' (if was isFirst, now isFirst is false, don't need to care)
		tokenizer.pushPeek(JsonToken(",", JsonToken.Type.structural))
		tokenizer.pushPeek(token)
		return token.type == JsonToken.Type.nullLiteral
	}
	
	override fun beginReadValue() {
		if(isFirst) isFirst = false
		else readStructure(",")
	}
	
	fun isEnd(): Boolean {
		val token = tokenizer.next()
		tokenizer.pushPeek(token)
		return token.text == "]" && token.type == JsonToken.Type.structural
	}
	
	override fun end(): Boolean {
		val token = tokenizer.next()
		if(token.text == "]" && token.type == JsonToken.Type.structural) return true
		tokenizer.pushPeek(token)
		return false
	}
}

class JsonAnyParser(config: JsonConfig, tokenizer: PeekableCloneableIterator<JsonToken>) :
	JsonParser(config, tokenizer) {
	constructor(config: JsonConfig, tokenizer: CloneableIterator<JsonToken>) : this(config, PeekableCloneableIterator(tokenizer))
	
	override fun peekIsNull(): Boolean {
		val token = readValue()
		if(token.type == JsonToken.Type.nullLiteral) return true
		tokenizer.pushPeek(token)
		return false
	}
}