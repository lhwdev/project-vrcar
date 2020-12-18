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
		val adapter = JsonAdapter(EmptySerializersModule, JsonConfig(prettyPrint = PrettyPrint(), isLenient = ConfigValue.enabled, writeValueWithoutQuote = true))
		println(
			adapter.decodeFromString(
				Hello.serializer(),
				adapter.encodeToString(
					Hello.serializer(),
					Hello("ho", "hi", Hello("a", "b", null, listOf()), listOf("a", "haha"))
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
	val hi: String,
	val b: String,
	val hello: Hello?,
	val list: List<String>
)
