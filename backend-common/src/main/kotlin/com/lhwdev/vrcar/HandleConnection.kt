package com.lhwdev.vrcar

import com.lhwdev.json.decoderFromStream
import com.lhwdev.json.encoderToStream
import com.lhwdev.json.serialization.JsonAdapter
import com.lhwdev.util.DisposeScope
import com.lhwdev.util.invoke
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.serialization.KSerializer
import java.io.Reader
import java.io.Writer



fun wrap(r: Reader, name: String = "") = object : Reader() {
	override fun read(cbuf: CharArray, off: Int, len: Int): Int {
		print("read$name ")
		val value = r.read(cbuf, off, len)
		println(
			String(cbuf, off, len) + " " + cbuf.sliceArray(off until off + len).joinToString { it.toInt().toString() })
		return value
	}
	
	override fun close() {
		r.close()
	}
}

fun wrap(w: Writer, name: String = "") = object : Writer() {
	override fun close() {
		w.close()
	}
	
	override fun flush() {
		w.flush()
	}
	
	override fun write(cbuf: CharArray, off: Int, len: Int) {
		print("write$name ")
		w.write(cbuf, off, len)
		println(
			String(cbuf, off, len) + " " + cbuf.sliceArray(off until off + len).joinToString { it.toInt().toString() })
	}
}

abstract class HandleConnection<T : Any, R : Any>(
	adapter: JsonAdapter,
	reader: Reader,
	writer: Writer,
	requestDataSerializer: KSerializer<T>,
	responseDataSerializer: KSerializer<R>
) {
	private val disposal = DisposeScope()
	private val decoder = adapter.decoderFromStream(disposal { reader })
	private val encoder = adapter.encoderToStream(disposal { writer })
	
	private val requestSerializer = Request.serializer(requestDataSerializer)
	private val responseSerializer = Response.serializer(responseDataSerializer)
	
	suspend fun main() {
		while(currentCoroutineContext().isActive) {
			val request = decoder.decodeSerializableValue(requestSerializer)
			if(request.data == null) {
				// response to close
				encoder.encodeSerializableValue(responseSerializer, Response(null, request.id))
				
				// stop connection
				close()
				return
			}
			
			println("HandleConnection: ${request.data}")
			
			val response = Response(onHandleInvocation(request.data), request.id)
			encoder.encodeSerializableValue(responseSerializer, response)
		}
	}
	
	
	protected abstract fun onHandleInvocation(request: T): R?
	
	private fun close() {
		disposal.dispose()
	}
}
