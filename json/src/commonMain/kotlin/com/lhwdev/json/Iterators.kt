package com.lhwdev.json


inline fun <T> Iterator<T>.nextOr(error: () -> Throwable) = if(hasNext()) next() else throw error()


fun <T> combinedIterator(a: T, b: Iterator<T>) = object : Iterator<T> {
	private var read = false
	override fun hasNext() = !read || b.hasNext()
	override fun next() = if(read) b.next() else a
}


class PeekableIterator<T>(private val original: Iterator<T>) : Iterator<T> {
	private val peekList = mutableListOf<T>()
	
	val allPeeks: List<T> get() = peekList
	
	fun pushPeek(peek: T) {
		peekList += peek
	}
	
	override fun hasNext() = peekList.isNotEmpty() || original.hasNext()
	
	override fun next() = (if(peekList.isNotEmpty()) {
		peekList.removeAt(0)
	} else original.next())
}


interface CloneableIterator<T> : Iterator<T> {
	val cloneSupported: Boolean get() = false
	fun cloneHere(): CloneableIterator<T>
}

class PeekableCloneableIterator<T>(private val original: CloneableIterator<T>) : CloneableIterator<T> {
	var count = 0
		private set
	private val peekList = mutableListOf<T>()
	
	val allPeeks: List<T> get() = peekList
	
	fun pushPeek(peek: T) {
		peekList += peek
	}
	
	override fun hasNext() = peekList.isNotEmpty() || original.hasNext()
	
	override val cloneSupported = original.cloneSupported
	override fun cloneHere(): PeekableCloneableIterator<T> {
		val cloned = PeekableCloneableIterator(original.cloneHere())
		cloned.count = count
		cloned.peekList += peekList
		return cloned
	}
	
	override fun next(): T {
		count++
		return if(peekList.isNotEmpty()) {
			peekList.removeAt(0)
		} else original.next()
	}
}