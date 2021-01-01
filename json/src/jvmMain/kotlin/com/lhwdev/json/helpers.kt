package com.lhwdev.json

import com.lhwdev.json.serialization.JsonAdapter
import com.lhwdev.json.serialization.JsonAnyDecoder
import com.lhwdev.json.serialization.JsonAnyEncoder
import java.io.Reader
import java.io.Writer



fun JsonAdapter.decoderFromStream(reader: Reader) =
	JsonAnyDecoder(this, JsonAnyParser(config, JsonTokenizerCloneable(config, reader.asIterator())))

fun JsonAdapter.encoderToStream(writer: Writer) =
	JsonAnyEncoder(this, JsonAnyWriter(config, writer.asCharOutput()))
