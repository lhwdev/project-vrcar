package com.lhwdev.json.serialization

import com.lhwdev.json.*
import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule


@OptIn(ExperimentalSerializationApi::class)
class JsonAdapter(
	override val serializersModule: SerializersModule,
	val config: JsonConfig
) : StringFormat {
	override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
		val tokenizer = JsonTokenizerCloneable(config, string.cloneableIterator())
		val parser = JsonAnyParser(config, PeekableCloneableIterator(tokenizer))
		
		val decoder = JsonRootDecoder(this, parser)
		return decoder.decodeSerializableValue(deserializer)
	}
	
	override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
		val output = StringBuilder()
		val writer = JsonAnyWriter(config, output.toCharOutput())
		
		val encoder = JsonAnyEncoder(this, writer)
		encoder.encodeSerializableValue(serializer, value)
		return output.toString()
	}
}