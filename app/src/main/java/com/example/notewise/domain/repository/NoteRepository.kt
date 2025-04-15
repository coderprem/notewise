package com.example.notewise.domain.repository

import com.example.notewise.data.local.dao.NoteDao
import com.example.notewise.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val dao: NoteDao
) {
    fun getAllNotes(): Flow<List<NoteEntity>> = dao.getAllNotes()

    suspend fun insertNote(note: NoteEntity) {
        dao.insert(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        dao.delete(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        dao.update(note)
    }
}
