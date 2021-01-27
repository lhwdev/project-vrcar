package com.lhwdev.vrcar

import com.github.sarxos.webcam.ds.ipcam.IpCamDeviceRegistry
import com.github.sarxos.webcam.ds.ipcam.IpCamMode
import kotlinx.coroutines.Dispatchers
import java.net.Socket
import kotlin.random.Random


// kinda like json rpc
class Controller(host: String, carPort: Int, camPort: Int) {
	private val cameraName = "vrCarCam" + hashCode() + "," + Random.nextLong()
	private val socket = Socket(host, carPort)
	
	val invokeConnection = InvokeConnection(
		sJsonAdapter,
		socket.getInputStream().bufferedReader(),
		socket.getOutputStream().bufferedWriter(),
		Packet.serializer(),
		Packet.serializer(),
		Dispatchers.IO
	)
	
	val camera = IpCamDeviceRegistry.register(cameraName, "http://$host:$camPort/camera/mjpeg", IpCamMode.PUSH)
	
	
	suspend fun connect() {
		check(invokeConnection(HelloPacket) == HelloPacket)
	}
	
	suspend fun speed(speed: Float) {
		invokeConnection(Speed(speed))
	}
	
	suspend fun stop() {
		speed(0f)
	}
	
	suspend fun steer(difference: Float, forward: Float = 0f) {
		invokeConnection(Steer(difference, forward))
	}
	
	// suspend fun sp
	
	suspend fun close() {
		invokeConnection.close()
		camera.close()
		IpCamDeviceRegistry.unregister(cameraName)
	}
}
