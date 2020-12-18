package com.lhwdev.compose.materialapp.repository

import kotlinx.serialization.json.Json


fun main() {
	val note = NoteImpl(NoteInfo("Hello, world!", "the world is revolving", 0L), NoteContent.Text("Hello!!!!!!!"))
	println(Json.encodeToString(NoteImpl.serializer(), note))
}
