package com.lhwdev.vrcar

import com.lhwdev.json.decoderFromStream
import com.lhwdev.json.encoderToStream
import com.lhwdev.json.serialization.JsonAdapter
import com.lhwdev.util.DisposeScope
import com.lhwdev.util.invoke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import java.io.IOException
import java.io.Reader
import java.io.Writer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext


class InvokeConnection<T : Any, R : Any>(
	adapter: JsonAdapter,
	val reader: Reader,
	writer: Writer,
	requestDataSerializer: KSerializer<T>,
	responseDataSerializer: KSerializer<R>,
	private val context: CoroutineContext = Dispatchers.IO
) {
	private val disposal = DisposeScope()
	private val lock = Mutex()
	
	private val decoder = adapter.decoderFromStream(disposal { reader })
	private val encoder = adapter.encoderToStream(disposal { writer })
	private var idCounter = 0
	
	private val requestSerializer = Request.serializer(requestDataSerializer)
	private val responseSerializer = Response.serializer(responseDataSerializer)
	
	private suspend fun send(request: T?): R? = withContext(context) {
		println("invoke $request")
		lock.withLock {
			val id = idCounter++
			encoder.encodeSerializableValue(requestSerializer, Request(request, id))
			val response = decoder.decodeSerializableValue(responseSerializer)
			check(response.id == id)
			response.data
		}
	}
	
	suspend operator fun invoke(request: T): R? = send(request)
	
	suspend fun close() {
		send(null)
		disposal.dispose()
	}
}
