package com.example.notewise.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screens {

    @Serializable
    data object NoteListScreen: Screens()

    @Serializable
    data class AddNoteScreen(val noteId: Int?): Screens()
}