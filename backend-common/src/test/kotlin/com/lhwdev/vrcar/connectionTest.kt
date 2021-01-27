package com.lhwdev.vrcar

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.io.PipedReader
import java.io.PipedWriter
import kotlin.concurrent.thread
import kotlin.test.Test


@Serializable
data class Data(val `true`: String)


class ConnectionTest {
	@Test
	fun `if json working or not`() {
		val encoded = sJsonAdapter.encodeToString(Data.serializer(), Data("123"))
		println(encoded)
		println(sJsonAdapter.decodeFromString(Data.serializer(), encoded))
	}
	
	@Test
	fun `piped test`() {
		val sendWriter = PipedWriter()
		val sendReader = PipedReader(sendWriter)
		
		val receiveWriter = PipedWriter()
		val receiveReader = PipedReader(receiveWriter)
		
		val invoker = InvokeConnection(sJsonAdapter, receiveReader, sendWriter, Data.serializer(), Data.serializer())
		val handler = object : HandleConnection<Data, Data>(
			sJsonAdapter,
			sendReader, receiveWriter,
			Data.serializer(), Data.serializer()
		) {
			override fun onHandleInvocation(request: Data) = Data(request.`true` + "!!")
		}
		
		val a = thread(name = "invoker") {
			runBlocking {
				delay(10)
				println("start invoker")
				println(invoker(Data("12345")))
				println(invoker(Data("123456")))
				println(invoker(Data("1234567")))
				invoker.close()
			}
		}
		
		val b = thread(name = "handler") {
			runBlocking {
				println("start handler")
				handler.main()
			}
		}
		
		a.join()
		b.join()
	}
}
