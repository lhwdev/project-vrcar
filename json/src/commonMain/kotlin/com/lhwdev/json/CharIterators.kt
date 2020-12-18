package com.lhwdev.json


fun CharIterator.take(n: Int) = buildString {
	for(i in 0 until n) {
		append(nextChar())
	}
}


fun mutableCharListOf() = mutableListOf<Char>() // TODO

fun PeekableCharIterator.peekOrNull() = if(hasNext()) peek() else null

class PeekableCharIterator(private val original: CharIterator) : CharIterator() {
	private val peekList = mutableCharListOf()
	
	val allPeeks: List<Char> get() = peekList
	
	fun pushPeek(peek: Char) {
		peekList += peek
	}
	
	override fun hasNext() = peekList.isNotEmpty() || original.hasNext()
	
	// values peeked several times are identical (unless nextChar() is not called)
	fun peek(): Char {
		val peek = original.nextChar()
		pushPeek(peek)
		return peek
	}
	
	override fun nextChar() = if(peekList.isNotEmpty()) {
		peekList.removeAt(0)
	} else original.nextChar()
}


abstract class CloneableCharIterator : CharIterator() {
	open val cloneSupported: Boolean get() = false
	abstract fun cloneHere(): CloneableCharIterator
}

fun CharSequence.cloneableIterator(startIndex: Int = 0): CloneableCharIterator = object : CloneableCharIterator() {
	private var index = startIndex
	override val cloneSupported get() = true
	
	override fun cloneHere() = cloneableIterator(index)
	override fun nextChar(): Char = get(index++)
	override fun hasNext(): Boolean = index < length
}
