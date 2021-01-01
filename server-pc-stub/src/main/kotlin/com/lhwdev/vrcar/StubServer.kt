package com.lhwdev.vrcar

import com.lhwdev.json.serialization.JsonAdapter
import java.io.Reader
import java.io.Writer
import kotlin.coroutines.CoroutineContext


class StubServer(adapter: JsonAdapter, reader: Reader, writer: Writer, context: CoroutineContext) :
	HandleConnection<Packet, Packet>(adapter, reader, writer, Packet.serializer(), Packet.serializer(), context) {
	override fun onHandleInvocation(request: Packet): Packet = when(request) {
		HelloPacket -> HelloPacket
		Ping -> Ping
		is Speed -> TODO()
		is Steer -> TODO()
	}
}
