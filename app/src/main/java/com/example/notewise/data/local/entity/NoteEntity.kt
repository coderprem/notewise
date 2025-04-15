package com.example.notewise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String?,
    val content: String,
    val timestamp: Long,
    val categories: List<String>,
    val isBookmarked: Boolean = false // ⬅️ New field
)
