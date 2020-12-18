package com.lhwdev.json.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder


@ExperimentalSerializationApi
fun SerialDescriptor.getElementIndexOrThrow(name: String): Int {
	val index = getElementIndex(name)
	if (index == CompositeDecoder.UNKNOWN_NAME)
		throw SerializationException("$serialName does not contain element with name '$name'")
	return index
}