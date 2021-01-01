package com.lhwdev.compose

import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope


@Composable
fun <T> lazyEffect(subject: Any?, initial: () -> T, onError: (() -> T)? = null, init: suspend CoroutineScope.() -> T): T? {
	var value: T? by remember { mutableStateOf(initial()) }
	
	LaunchedEffect(subject) {
		value = try {
			init()
		} catch(e: Throwable) {
			if(onError == null) throw e
			onError()
		}
	}
	
	return value
}

@Composable
fun <T> lazyEffect(vararg subject: Any?, initial: T, onError: (() -> T)? = null, init: suspend CoroutineScope.() -> T): T? {
	var value: T? by remember { mutableStateOf(initial) }
	
	LaunchedEffect(*subject) {
		value = try {
			init()
		} catch(e: Throwable) {
			if(onError == null) throw e
			onError()
		}
	}
	
	return value
}
