package com.lhwdev.vrcar.client

import com.lhwdev.pair


data class CarIdInfo(val host: String, val port: Int)

fun CarIdInfo(string: String) = runCatching {
	string.pair(":").let { (host, port) -> CarIdInfo(host, port.toInt()) }
}.getOrNull()
