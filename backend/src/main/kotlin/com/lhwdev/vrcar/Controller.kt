package com.lhwdev.vrcar

import com.lhwdev.json.*
import com.lhwdev.util.DisposeScope
import java.net.Socket
import com.lhwdev.util.invoke
import java.io.Closeable


private val sJsonConfig = JsonConfig()


// kinda like json rpc
class Controller(host: String, port: Int): Closeable {
	private val disposal = DisposeScope()
	private val socket = Socket(host, port)
	private val parser = JsonAnyParser(
		sJsonConfig,
		JsonTokenizerCloneable(sJsonConfig, disposal { socket.getInputStream().bufferedReader() }.asIterator())
	)
	private val writer = JsonAnyWriter(
		sJsonConfig,
		disposal { socket.getOutputStream().bufferedWriter() }.asCharOutput()
	)
	
	init {
	
	}
	
	override fun close() {
		disposal.dispose()
	}
}
