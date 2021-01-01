package com.lhwdev.json.serialization

import com.lhwdev.json.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule


@OptIn(ExperimentalSerializationApi::class)
class JsonAdapter(
	val config: JsonConfig,
	override val serializersModule: SerializersModule = EmptySerializersModule
) : StringFormat {
	override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
		val tokenizer = JsonTokenizerCloneable(config, string.cloneableIterator())
		val parser = JsonAnyParser(config, PeekableCloneableIterator(tokenizer))
		
		val decoder = JsonAnyDecoder(this, parser)
		return decoder.decodeSerializableValue(deserializer)
	}
	
	override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String {
		val output = StringBuilder()
		val writer = JsonAnyWriter(config, output.toCharOutput())
		
		val encoder = JsonAnyEncoder(this, writer)
		encoder.encodeSerializableValue(serializer, value)
		return output.toString()
	}
	
	fun decoderFromStream(iterator: CloneableCharIterator) =
		JsonAnyDecoder(this, JsonAnyParser(config, JsonTokenizerCloneable(config, iterator)))
	
	fun encoderToStream(output: CharOutput) =
		JsonAnyEncoder(this, JsonAnyWriter(config, output))
}
