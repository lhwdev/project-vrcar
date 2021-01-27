package com.lhwdev.utils


inline fun <T> merge(a: T?, b: T?, onMerge: (a: T, b: T) -> T): T? = when {
	a == null -> b
	b == null -> a
	else -> onMerge(a, b)
}
