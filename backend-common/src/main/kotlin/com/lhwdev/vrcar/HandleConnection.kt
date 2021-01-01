package com.lhwdev.vrcar

import com.lhwdev.json.decoderFromStream
import com.lhwdev.json.encoderToStream
import com.lhwdev.json.serialization.JsonAdapter
import com.lhwdev.util.DisposeScope
import com.lhwdev.util.invoke
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.KSerializer
import java.io.Closeable
import java.io.Reader
import java.io.Writer
import kotlin.coroutines.CoroutineContext


abstract class HandleConnection<T : Any, R : Any>(
	adapter: JsonAdapter,
	reader: Reader,
	writer: Writer,
	requestDataSerializer: KSerializer<T>,
	responseDataSerializer: KSerializer<R>
) : Closeable {
	private val disposal = DisposeScope()
	private val decoder = adapter.decoderFromStream(disposal { reader })
	private val encoder = adapter.encoderToStream(disposal { writer })
	
	private val requestSerializer = Request.serializer(requestDataSerializer)
	private val responseSerializer = Response.serializer(responseDataSerializer)
	
	suspend fun main() {
		while(currentCoroutineContext().isActive) {
			val request = decoder.decodeSerializableValue(requestSerializer)
			if(request.data == null) break // stop connection
			encoder.encodeSerializableValue(responseSerializer, Response(onHandleInvocation(request.data), request.id))
		}
	}
	
	
	protected abstract fun onHandleInvocation(request: T): R?
	
	override fun close() {
		disposal.dispose()
	}
}
