package com.lhwdev.compose.utils

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope


@Composable
fun <T> lazyEffect(
	subject: Any?,
	initial: () -> T,
	onError: ((Throwable) -> T)? = null,
	init: suspend CoroutineScope.() -> T
): T? {
	var value: T? by remember { mutableStateOf(initial()) }
	
	LaunchedEffect(subject) {
		value = try {
			init()
		} catch(e: Throwable) {
			if(onError == null) throw e
			onError(e)
		}
	}
	
	return value
}

@Composable
fun <T> lazyEffect(
	vararg subject: Any?,
	initial: T,
	onError: ((Throwable) -> T)? = null,
	init: suspend CoroutineScope.() -> T
): T? {
	var value: T? by remember { mutableStateOf(initial) }
	
	LaunchedEffect(*subject) {
		value = try {
			init()
		} catch(e: Throwable) {
			if(onError == null) throw e
			onError(e)
		}
	}
	
	return value
}
