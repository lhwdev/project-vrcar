package com.lhwdev.vrcar

import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.html.body
import kotlinx.html.video
import org.bytedeco.ffmpeg.global.avcodec
import org.bytedeco.javacpp.Loader
import org.bytedeco.javacpp.presets.javacpp
import org.bytedeco.javacv.*
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.video.Video
import org.opencv.videoio.VideoCapture
import org.opencv.videoio.VideoWriter
import org.opencv.videoio.Videoio
import java.awt.image.BufferedImage
import java.io.*
import java.lang.Runnable
import java.net.ServerSocket
import java.net.Socket
import javax.imageio.ImageIO
import javax.swing.Timer


private val sInitialization = run {
	Loader.load(org.bytedeco.opencv.opencv_java::class.java)
	// Loader.load(JavaCV)
}



class CameraStreamReader(host: String, port: Int) {
	private val socket = Socket(host, port)
	private val invokeConnection = InvokeConnection(
		sJsonAdapter,
		socket.getInputStream().reader(),
		socket.getOutputStream().writer(),
		CameraRequestPacket.serializer(),
		CameraResponsePacket.serializer()
	)
	
	init {
		sInitialization
	}
}


suspend fun main() {
	mainCamera()
}

suspend fun mainCamera() = coroutineScope {
	
	launch {
		httpCameraStream(1, 8080)
	}
	
	launch {
		httpWebsite(8081)
	}
}

fun httpWebsite(port: Int) = embeddedServer(Netty, port) {
	routing {
		get("/") {
			call.respondHtml {
				body {
					video {
						src = "rtmp://:8080"
					}
				}
			}
		}
	}
}.start()

// https://github.com/bytedeco/javacv/blob/master/samples/WebcamAndMicrophoneCapture.java
suspend fun httpCameraStream(deviceIndex: Int, port: Int) = withContext(Dispatchers.IO) main@ {
	val server = ServerSocket(port)
	
	val grabber = OpenCVFrameGrabber.createDefault(deviceIndex)
	grabber.start()
	
	while(isActive) {
		val client = server.accept()
		println("Accepted ${client.inetAddress}")
		
		val recorder = FFmpegFrameRecorder(client.getOutputStream(), 2)
		
		recorder.setVideoOption("preset", "ultrafast")
		recorder.videoCodec = avcodec.AV_CODEC_ID_H264
		recorder.format = "mp4"
		
		recorder.start()
		
		val canvasFrame = CanvasFrame("Preview")
		
		val startTime = System.currentTimeMillis()
		
		while(isActive) {
			val frame = grabber.grab() ?: return@main
			if(canvasFrame.isVisible) canvasFrame.showImage(frame)
			val timeStamp = 1000 * (System.currentTimeMillis() - startTime)
			
			if(timeStamp > recorder.timestamp)
				recorder.timestamp = timeStamp
			
			recorder.record(frame)
		}
	}
}



class HttpStreamServer(var imag: Mat) : Runnable {
	private val img: BufferedImage? = null
	private lateinit var serverSocket: ServerSocket
	private lateinit var socket: Socket
	private val boundary = "stream"
	private lateinit var outputStream: OutputStream
	
	
	init {
		sInitialization
	}
	
	
	@Throws(IOException::class)
	fun startStreamingServer() {
		serverSocket = ServerSocket(8080)
		socket = serverSocket.accept()
		writeHeader(socket.getOutputStream(), boundary)
	}
	
	@Throws(IOException::class)
	private fun writeHeader(stream: OutputStream, boundary: String) {
		stream.write(
			"""HTTP/1.0 200 OK
	Connection: close
	Max-Age: 0
	Expires: 0
	Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0
	Pragma: no-cache
	Content-Type: multipart/x-mixed-replace; boundary=$boundary
	
	--$boundary
	""".toByteArray()
		)
	}
	
	@Throws(IOException::class)
	fun pushImage(frame: Mat?) {
		if(frame == null) return
		try {
			outputStream = socket.getOutputStream()
			val img = Mat2bufferedImage(frame)
			val baos = ByteArrayOutputStream()
			ImageIO.write(img, "jpg", baos)
			val imageBytes = baos.toByteArray()
			outputStream.write(
				("""Content-type: image/jpeg
Content-Length: ${imageBytes.size}

""").toByteArray()
			)
			outputStream.write(imageBytes)
			outputStream.write("/r/n--$boundary/r/n".toByteArray())
		} catch(ex: Exception) {
			socket = serverSocket.accept()
			writeHeader(socket.getOutputStream(), boundary)
		}
	}
	
	override fun run() {
		try {
			print("go to http://localhost:8080 with browser")
			startStreamingServer()
			while(true) {
				pushImage(imag)
			}
		} catch(e: IOException) {
			return
		}
	}
	
	@Throws(IOException::class)
	fun stopStreamingServer() {
		socket.close()
		serverSocket.close()
	}
	
	companion object {
		@Throws(IOException::class)
		fun Mat2bufferedImage(image: Mat?): BufferedImage? {
			val bytemat = MatOfByte()
			
			Imgcodecs.imencode(".jpg", image, bytemat)
			val bytes = bytemat.toArray()
			val `in`: InputStream = ByteArrayInputStream(bytes)
			return ImageIO.read(`in`)
		}
	}
}


object OpenCVCameraStream {
	//region Properties
	lateinit var frame: Mat
	private lateinit var httpStreamService: HttpStreamServer
	lateinit var videoCapture: VideoCapture
	lateinit var tmrVideoProcess: Timer
	
	
	init {
		sInitialization
	}
	
	
	//endregion
	//region Methods
	fun start() {
		videoCapture = VideoCapture()
		videoCapture.open(0)
		if(!videoCapture.isOpened) {
			return
		}
		frame = Mat()
		httpStreamService = HttpStreamServer(frame)
		Thread(httpStreamService).start()
		
		tmrVideoProcess = Timer(100) {
			val time = System.currentTimeMillis()
			if(!videoCapture.read(frame)) {
				tmrVideoProcess.stop()
			}
			
			//procesed image
			httpStreamService.imag = frame
		}
		tmrVideoProcess.start()
	}
	
	@JvmStatic
	fun main(args: Array<String>) {
		start()
	}
}
