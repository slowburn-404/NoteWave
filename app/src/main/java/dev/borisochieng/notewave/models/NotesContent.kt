package dev.borisochieng.notewave.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes_table")
data class NotesContent(
    @PrimaryKey(autoGenerate = true) val noteId: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "updatedAt") val updatedAt: String
)
