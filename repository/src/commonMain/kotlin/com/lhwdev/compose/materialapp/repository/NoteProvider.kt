package com.lhwdev.compose.materialapp.repository

import kotlinx.serialization.Serializable


expect fun getDefaultNoteProvider(): NoteProvider

interface NoteProvider {
	suspend fun getNotes(): List<Note>
}

interface Note {
	val info: NoteInfo
	val content: NoteContent
}

@Serializable
sealed class NoteContent {
	@Serializable
	class Text(var text: String) : NoteContent()

//	@Serializable
//	class Image(val imageAsset: ImageAsset) : NoteContent()

//	@Serializable
//	class Items(val items: MutableList<NoteContent>) : NoteContent()
}

@Serializable
data class NoteInfo(val title: String, val description: String, val color: Long)
