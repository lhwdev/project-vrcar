package com.lhwdev.json.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.*


class LazySequenceSerializer<T>(private val itemSerializer: KSerializer<T>) : KSerializer<Sequence<T>> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor = object : SerialDescriptor by listSerialDescriptor(itemSerializer.descriptor) {
		override val serialName = LazySequenceSerializer::class.qualifiedName!!
	}
	
	override fun deserialize(decoder: Decoder): Sequence<T> = if(decoder is JsonDecoder) {
		val compositeDecoder = decoder.beginStructurePointed(descriptor) as JsonListDecoder
		sequence {
			// don't need to use decodeSequentially
			while(compositeDecoder.decodeElementIndex(descriptor) != CompositeDecoder.DECODE_DONE) yield(
				compositeDecoder.decodeSerializableElement(
					descriptor,
					compositeDecoder.currentIndex,
					itemSerializer
				)
			)
			compositeDecoder.endStructure(descriptor)
		}
	} else decoder.decodeStructure(descriptor) {
		@OptIn(ExperimentalSerializationApi::class)
		if(decodeSequentially()) {
			val size = decodeCollectionSize(descriptor)
			ArrayList<T>(size).apply {
				for(index in 0 until size) add(decodeSerializableElement(descriptor, index, itemSerializer))
			}
		} else {
			val size = decodeCollectionSize(descriptor)
			val list: MutableList<T> = if(size == -1) mutableListOf() else ArrayList(size)
			while(true) {
				val index = decodeElementIndex(descriptor)
				if(index == CompositeDecoder.DECODE_DONE) break
				list.add(index, decodeSerializableElement(descriptor, index, itemSerializer))
			}
			list
		}
	}.asSequence()
	
	override fun serialize(encoder: Encoder, value: Sequence<T>) {
		encoder.encodeStructure(descriptor) {
			value.forEachIndexed { index, element ->
				encodeSerializableElement(descriptor, index, itemSerializer, element)
			}
		}
	}
}

// TODO
class LazyListSerializer<T>(private val itemSerializer: KSerializer<T>) : KSerializer<List<T>> {
	@OptIn(ExperimentalSerializationApi::class)
	override val descriptor = object : SerialDescriptor by listSerialDescriptor(itemSerializer.descriptor) {
		override val serialName = LazyListSerializer::class.qualifiedName!!
	}
	
	@OptIn(ExperimentalSerializationApi::class)
	override fun deserialize(decoder: Decoder) = if(decoder is JsonDecoder) object : AbstractList<T>() {
		private val compositeDecoder = decoder.beginStructurePointed(descriptor)
		private val isSequentially = compositeDecoder.decodeSequentially()
		private val cache = mutableListOf<T>()
		
		override val size = compositeDecoder.decodeCollectionSize(descriptor)
		
		private fun next() {
			if(isSequentially) {
				cache += compositeDecoder.decodeSerializableElement(descriptor, cache.size, itemSerializer)
			} else {
				val index = compositeDecoder.decodeElementIndex(descriptor)
				cache.add(index, compositeDecoder.decodeSerializableElement(descriptor, index, itemSerializer))
			}
		}
		
		override fun get(index: Int): T {
			while(cache.size <= index) {
				next()
			}
			return cache[index]
		}
	} else decoder.decodeStructure(descriptor) {
		if(decodeSequentially()) {
			val size = decodeCollectionSize(descriptor)
			val list = ArrayList<T>(size)
			for(index in 0 until size) list += decodeSerializableElement(descriptor, index, itemSerializer)
			list
		} else  {
			val size = decodeCollectionSize(descriptor)
			val list: MutableList<T> = if(size == -1) mutableListOf() else ArrayList(size)
			while(true) {
				val index = decodeElementIndex(descriptor)
				if(index == CompositeDecoder.DECODE_DONE) break
				list.add(index, decodeSerializableElement(descriptor, index, itemSerializer))
			}
			list
		}
	}
	
	override fun serialize(encoder: Encoder, value: List<T>) {
		ListSerializer(itemSerializer)
	}
}
