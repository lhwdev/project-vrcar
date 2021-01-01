package com.lhwdev.vrcar.rpi

import com.lhwdev.vrcar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket


fun main() {
	println(System.getProperty("os.name"))
	
	runBlocking {
		motorMain()
	}
}

suspend fun motorMain() {
	val motor = CarMotor()
	val socket = ServerSocket(2536)
	
	while(true) {
		// motor: only one client
		val client = withContext(Dispatchers.IO) { socket.accept() }
		accept(motor, client)
		motor.reset()
	}
}

suspend fun accept(motor: CarMotor, client: Socket) = withContext(Dispatchers.IO) {
	val handle = object : HandleConnection<Packet, Packet>(
		sJsonAdapter,
		client.getInputStream().reader(),
		client.getOutputStream().writer(),
		Packet.serializer(),
		Packet.serializer()
	) {
		override fun onHandleInvocation(request: Packet): Packet? {
			when(request) {
				HelloPacket -> return HelloPacket
				Ping -> return Ping
				is Speed -> motor.speed = request.speed
				is Steer -> {
					motor.steeringDifference = request.difference
					motor.steeringForward = request.forward
				}
			}
			
			return null
		}
	}
	
	handle.main()
	handle.close()
}
