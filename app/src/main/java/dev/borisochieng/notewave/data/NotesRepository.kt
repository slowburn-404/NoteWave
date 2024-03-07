package dev.borisochieng.notewave.data

import androidx.annotation.WorkerThread
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.data.local.database.NotesDao
import kotlinx.coroutines.flow.Flow

class NotesRepository(private val notesDao: NotesDao) {

    /*  Room executes all the queries on a separate thread
        and the observed flow will notify the observer when
        the data has changed
    */
    val getAllNotes: Flow<MutableList<Note>> = notesDao.getAllNotes()

    @WorkerThread
    suspend fun addNewNote(note: Note) {
        notesDao.addNewNote(note)
    }

    @WorkerThread
    suspend fun editNote(note: Note) {
        notesDao.editNote(note)
    }

    @WorkerThread
    suspend fun deleteNote(note: Note) {
        notesDao.deleteNote(note)
    }

    @WorkerThread
    fun getNoteById(noteId: Long): Flow<Note?> = notesDao.getNoteById(noteId)

}