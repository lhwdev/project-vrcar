package com.lhwdev.compose.materialapp.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File


private val sFormat = Json


actual fun getDefaultNoteProvider(): NoteProvider = FileNoteProvider(File("./note-data.json").canonicalFile)

class FileNoteProvider(val file: File) : NoteProvider {
	init {
		if(!file.exists()) file.writeText(sFormat.encodeToString(NoteList.serializer(), NoteList(listOf())))
	}
	
	override suspend fun getNotes() = withContext(Dispatchers.IO) {
		
		sFormat.decodeFromString(NoteList.serializer(), file.readText()).notes
	}
}

@Serializable
private data class NoteList(val notes: List<NoteImpl>)

@Serializable
data class NoteImpl(override val info: NoteInfo, override val content: NoteContent) : Note
