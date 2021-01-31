package com.lhwdev.vrcar

import kotlinx.coroutines.Dispatchers
import java.net.Socket
import kotlin.random.Random


// kinda like json rpc
class Controller(val host: String, carPort: Int, val camPort: Int) {
	val cameraName = "vrCarCam" + hashCode() + "," + Random.nextLong()
	private val socket = Socket(host, carPort)
	
	val invokeConnection = InvokeConnection(
		sJsonAdapter,
		socket.getInputStream().bufferedReader(),
		socket.getOutputStream().bufferedWriter(),
		Packet.serializer(),
		Packet.serializer(),
		Dispatchers.IO
	)
	
	val cameraUrl get() = "http://$host:$camPort/camera/mjpeg"
	
	
	private suspend fun invokeItem(packet: Packet): Packet? {
		println(packet)
		return invokeConnection(packet)
	}
	
	
	suspend fun connect() {
		check(invokeItem(HelloPacket) == HelloPacket)
	}
	
	suspend fun speed(speed: Float) {
		invokeItem(Speed(speed))
	}
	
	suspend fun stop() {
		speed(0f)
	}
	
	suspend fun steer(difference: Float, forward: Float = 0f) {
		invokeItem(Steer(difference, forward))
	}
	
	suspend fun raw(left: Float, right: Float) {
		invokeItem(RawMotor(left, right))
	}
	
	// suspend fun sp
	
	suspend fun close() {
		invokeConnection.close()
	}
}
