package com.lhwdev.vrcar

import com.lhwdev.util.DisposeScope
import kotlinx.coroutines.Dispatchers
import java.io.Closeable
import java.net.Socket


// kinda like json rpc
class Controller(host: String, port: Int) : Closeable {
	private val disposal = DisposeScope()
	private val socket = Socket(host, port)
	
	val invokeConnection = InvokeConnection(
		sJsonAdapter,
		socket.getInputStream().bufferedReader(),
		socket.getOutputStream().bufferedWriter(),
		Packet.serializer(),
		Packet.serializer(),
		Dispatchers.IO
	)
	
	suspend fun connect() {
		check(invokeConnection(HelloPacket) == HelloPacket)
	}
	
	override fun close() {
		disposal.dispose()
	}
}
