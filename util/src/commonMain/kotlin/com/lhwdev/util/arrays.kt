package com.lhwdev.util


@Suppress("NOTHING_TO_INLINE")
inline fun <T> Array<T>.swap(newElement: T, index: Int): T {
	val last = this[index]
	this[index] = newElement
	return last
}
