package dev.borisochieng.notewave.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.borisochieng.notewave.models.NotesContent
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes_table ORDER BY noteId DESC")
    fun getAllNotes(): Flow<MutableList<NotesContent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewNote(note: NotesContent)

    @Update
    suspend fun editNote(notes: NotesContent)

    @Delete
    suspend fun deleteNote(note: NotesContent)
}