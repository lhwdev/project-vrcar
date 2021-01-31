package com.lhwdev.vrcar.rpi

import com.lhwdev.vrcar.*
import kotlinx.coroutines.*
import mhashim6.pi4k.gpioShutdown
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread
import kotlin.coroutines.resume


fun main() {
	println(System.getProperty("os.name"))
	
	
	runBlocking {
		launch { cameraMain() }
		motorMain()
	}
}

suspend fun cameraMain() = withContext(Dispatchers.IO) {
	suspendCancellableCoroutine<Unit> { cont ->
		val process = ProcessBuilder().apply {
			command("/home/pi/vrcar/cam2web")
		}.start()
		
		cont.invokeOnCancellation { process.destroy() }
		
		thread {
			process.waitFor()
			cont.resume(Unit)
		}
	}
}

suspend fun motorMain() = try {
	val motor = CarMotor()
	
	val socket = ServerSocket(2536)
	
	while(true) {
		// motor: only one client
		val client = withContext(Dispatchers.IO) { socket.accept() }
		try {
			accept(motor, client)
		} catch(e: Throwable) {
		}
		motor.reset()
	}
} finally {
	println("shutdown")
	gpioShutdown()
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
				is RawMotor -> {
					motor.updateForce(request.left, request.right)
				}
			}
			
			return null
		}
	}
	
	println("Connected to ${client.inetAddress}")
	try {
		handle.main()
	} catch(e: Throwable) {
		e.printStackTrace()
	}
	println("Disconnected")
}
