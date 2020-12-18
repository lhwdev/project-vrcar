package com.lhwdev.compose.materialapp.repository

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure


@OptIn(ExperimentalSerializationApi::class)
class SequenceSerializer<T>(val elementDescriptor: SerialDescriptor, val elementSerializer: SerializationStrategy<T>) : KSerializer<Sequence<T>> {
    override val descriptor = listSerialDescriptor(elementDescriptor)

    override fun deserialize(decoder: Decoder) = TODO()

    override fun serialize(encoder: Encoder, value: Sequence<T>) {
        val descriptor = elementDescriptor
        encoder.encodeStructure(descriptor) {
            value.forEachIndexed { index, item ->
                encodeSerializableElement(elementDescriptor, index, elementSerializer, item)
            }
        }
    }
}