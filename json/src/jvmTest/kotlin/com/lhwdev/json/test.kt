package com.lhwdev.json

import com.lhwdev.json.serialization.JsonAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import java.io.PipedReader
import java.io.PipedWriter
import kotlin.concurrent.thread
import kotlin.test.Test



@Serializable
data class Hello(
	val map: Map<String, String>,
	val a: String,
	val b: Int,
	val c: List<Float>
)

class Test {
	@OptIn(ExperimentalSerializationApi::class)
	@Test
	fun `serialization test`() {
		val adapter = JsonAdapter(
			JsonConfig(noQuote = ConfigValue.enabled)
		)
		
		val sample = Hello(mapOf("1" to "hi", "2" to "ho"), "Jack", 123, listOf(1f, 3.5f, 312503f))
		
		run {
			val encoded = adapter.encodeToString(Hello.serializer(), sample)
			println(encoded)
			val decoded = adapter.decodeFromString(Hello.serializer(), encoded)
			println(decoded)
		}
		
		val w = PipedWriter()
		val r = PipedReader(w)
		
		val a = thread {
			Thread.sleep(10)
			adapter.encoderToStream(w)
				.encodeSerializableValue(Hello.serializer(), sample)
		}
		
		val b = thread {
			println(adapter.decoderFromStream(r).decodeSerializableValue(Hello.serializer()))
		}
		
		a.join()
		b.join()
	}
	
	@Test
	fun `test comment`() {
		val text = """
			{"map": {}, "a": "ho", /* hello */ "b": 123, "c": []}
		""".trimIndent()
		val result =
			JsonAdapter(JsonConfig(isCommentAllowed = ConfigValue.enabled)).decodeFromString(Hello.serializer(), text)
		println(result)
	}
	
}
