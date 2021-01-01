package com.lhwdev.json

import com.lhwdev.json.serialization.JsonAdapter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.EmptySerializersModule
import kotlin.test.Test


class Test {
	@OptIn(ExperimentalSerializationApi::class)
	@Test
	fun `serialization test`() {
		val adapter = JsonAdapter(
			JsonConfig(prettyPrint = PrettyPrint(), noQuote = ConfigValue.enabled, writeValueWithoutQuote = true),
			EmptySerializersModule
		)
		println(
			adapter.decodeFromString(
				Hello.serializer(),
				adapter.encodeToString(
					Hello.serializer(),
					Hello(mapOf("1" to "hi", "2" to "ho"))
				).also {
					println(it)
				}
			)
		)
	}
	
	@Test
	fun `actual use cases directly on writer and parser`() {
		val data = """ {a: hi, b: 123} """
		// val str =
	}
}


@Serializable
data class Hello(
	val map: Map<String, String>
)
