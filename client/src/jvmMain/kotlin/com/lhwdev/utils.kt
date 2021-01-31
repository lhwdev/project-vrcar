package com.lhwdev

import com.lhwdev.utils.Mergeable


inline fun <reified T> List<*>.lastInstanceOf() = last { it is T } as T

inline fun <T, R : Any> List<T>.chooseNonNull(transform: (T) -> R?): R {
	for(i in indices) {
		val value = transform(get(i))
		if(value != null) return value
	}
	
	throw IllegalStateException("chooseLastNonNull: no matching item")
}

inline fun <T, R : Any> List<T>.chooseLastNonNull(transform: (T) -> R?): R {
	for(i in size - 1 downTo 0) {
		val value = transform(get(i))
		if(value != null) return value
	}
	
	throw IllegalStateException("chooseLastNonNull: no matching item")
}

inline fun <T, R : Mergeable<R>> Iterable<T>.foldMerged(initial: R, operation: (T) -> R?) = fold(initial) { acc, element ->
	val value = operation(element)
	if(value == null) acc else acc.merge(value)
}

fun <T : Mergeable<T>> Iterable<T>.mergeAll(initial: T) = foldMerged(initial) { it }

fun String.pair(by: String): Pair<String, String> {
	val index = indexOf(by)
	if(index == -1) error("couldn't find $by in $this")
	return substring(0, index) to drop(index + by.length)
}
