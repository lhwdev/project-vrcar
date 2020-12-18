package com.lhwdev.json.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


class LazySequenceSerializer<T>(private val itemSerializer: KSerializer<T>) : KSerializer<Sequence<T>> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor = listSerialDescriptor(itemSerializer.descriptor)
	
	override fun deserialize(decoder: Decoder): Sequence<T> {
		require(decoder is JsonDecoder)
		val compositeDecoder = decoder.beginStructurePointed(descriptor) as JsonListDecoder
		return sequence {
			// this can be only applied to JsonArrayParser
			while(compositeDecoder.decodeElementIndex(descriptor) != CompositeDecoder.DECODE_DONE)
				yield(
					compositeDecoder.decodeSerializableElement(
						descriptor,
						compositeDecoder.currentIndex,
						itemSerializer
					)
				)
		}
	}
	
	override fun serialize(encoder: Encoder, value: Sequence<T>) {
		TODO()
	}
}
