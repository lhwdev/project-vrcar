package com.lhwdev.json


data class JsonToken(val text: String, val type: JsonToken.Type) {
	enum class Type(val necessary: Boolean = true) {
		structural,
		string,
		booleanLiteral, nullLiteral, numberLiteral,
		whitespace(necessary = false), comment(necessary = false)
	}
	
	override fun toString() = when(type) {
		Type.structural -> "\u001b[36m$text\u001B[0m"
		Type.string -> "\u001B[31m\"$text\"\u001B[0m"
		Type.booleanLiteral, Type.nullLiteral, Type.numberLiteral -> "\u001B[92m$text\u001B[0m"
		Type.comment -> "\u001B[32m$text\u001B[0m"
		else -> text
	}
}

fun JsonToken.require(text: String, type: JsonToken.Type) {
	require(type)
	require(this.text == text) { "token text not match: expected $text but received $this" }
}

fun JsonToken.require(type: JsonToken.Type) {
	require(this.type == type) { "token type not match: expected $type but received $this" }
}


private fun malformed(string: String): Nothing = throw MalformedJsonException(string)
private fun notSymmetry(): Nothing = malformed("not closed")
private fun <T> Iterator<T>.nextOrNotSymmetry() = nextOr { MalformedJsonException("not closed") }

// http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf
internal const val sWhitespaces = "\u0009\u000A\u000D\u0020" // = "\t\r\n "


class JsonTokenizer(val config: JsonConfig, data: CharIterator) : Iterator<JsonToken> {
	private val reader = PeekableCharIterator(data)
	
	override fun hasNext() = reader.hasNext()
	
	override fun next(): JsonToken {
		while(true) {
			val token = nextWithoutFilter()
			if(token.type.necessary) return token
		}
	}
	
	fun nextWithoutFilter(): JsonToken {
		val char = reader.nextChar()
		val hasNext = reader.hasNext()
		
		when(char) {
			'\"' -> return buildString {
				if(!hasNext) notSymmetry()
				for(c in reader) {
					when(c) {
						'\\' -> {
							val escapeSequence = reader.nextOrNotSymmetry()
							when(escapeSequence) {
								'\"' -> append('\"')
								'\\' -> append('\\')
								'/' -> append('/')
								'b' -> append('\b')
								'f' -> append('\u000C')
								'n' -> append('\n')
								'r' -> append('\r')
								't' -> append('\t')
								'u' -> { // unicode /uXXXX
									append(reader.take(4).toInt(16).toChar())
								}
								else -> malformed("unknown escape sequence")
							}
						}
						'\"' -> break
						else -> append(c)
					}
				}
			}.let { JsonToken(it, JsonToken.Type.string) }
			'{', '}', '[', ']', ':', ',' -> return JsonToken("$char", JsonToken.Type.structural)
			'-', in '0'..'9' -> return buildString {
				append(char)
				for(c in reader) {
					if(c !in "-0123456789.eE+") {
						reader.pushPeek(c)
						break
					}
				}
			}.let { JsonToken(it, JsonToken.Type.numberLiteral) }
			't' -> if(nextExact("true")) return  JsonToken("true", JsonToken.Type.booleanLiteral)
			'f' -> if(nextExact("false")) return JsonToken("false", JsonToken.Type.booleanLiteral)
			'n' -> if(nextExact("null")) return JsonToken("null", JsonToken.Type.nullLiteral)
			in sWhitespaces -> return JsonToken("$char", JsonToken.Type.whitespace) // do not combine; don't need to
			'/' -> {
				val peek = reader.peekOrNull()
				return if(config.isCommentAllowed.isAllowed && peek == '/') buildString {
					for(c in reader) {
						if(c in "\r\n") break
						append(c)
					}
				}.let { JsonToken("/$it", JsonToken.Type.comment) }
				else if(config.isCommentAllowed.isAllowed && peek == '*') buildString {
					for(c in reader) {
						if(c == '*' && reader.peek() == '/') break
						append(c)
					}
				}.let { JsonToken("/$it", JsonToken.Type.comment) }
				else {
					if(peek == null) notSymmetry()
					reader.pushPeek(char)
					readStringLenient()
				}
			}
		}
		reader.pushPeek(char)
		return readStringLenient()
	}
	
	private fun nextExact(expect: String) = reader.take(expect.length - 1) == expect.drop(1)
	
	private fun readStringLenient(): JsonToken = if(config.isLenient.isAllowed) buildString {
		for(char in reader) {
			if(char in "[]{}:,$sWhitespaces") {
				reader.pushPeek(char)
				break
			}
			append(char)
		}
	}.let { JsonToken(it, JsonToken.Type.string) } else malformed("")
}

fun Iterator<JsonToken>.next(type: JsonToken.Type) = next().also { it.require(type) }

fun Iterator<JsonToken>.next(text: String, type: JsonToken.Type) = next().also { it.require(text, type) }


fun JsonTokenizerCloneable(
	config: JsonConfig, text: CloneableCharIterator
): CloneableIterator<JsonToken> = object : CloneableIterator<JsonToken> {
	private val parser = JsonTokenizer(config, text)
	
	override val cloneSupported = text.cloneSupported
	override fun cloneHere() = JsonTokenizerCloneable(config, text.cloneHere())
	
	override fun hasNext() = parser.hasNext()
	override fun next() = parser.next()
}