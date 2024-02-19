package dev.borisochieng.notewave.repositories

import androidx.annotation.WorkerThread
import dev.borisochieng.notewave.models.Note
import dev.borisochieng.notewave.database.NotesDao
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

}