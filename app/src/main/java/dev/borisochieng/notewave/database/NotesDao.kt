package dev.borisochieng.notewave.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.borisochieng.notewave.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes_table ORDER BY noteId DESC")
    fun getAllNotes(): Flow<MutableList<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNewNote(note: Note)

    @Update
    suspend fun editNote(notes: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}