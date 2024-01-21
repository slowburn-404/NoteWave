package dev.borisochieng.notewave.repositories

import androidx.annotation.WorkerThread
import dev.borisochieng.notewave.models.NotesContent
import dev.borisochieng.notewave.database.NotesDao
import kotlinx.coroutines.flow.Flow

class NotesRepository(private val notesDao: NotesDao) {

    /*  Room executes all the queries on a separate thread
        and the observed flow will notify the observer when
        the data has changed
    */
    val getAllNotes: Flow<MutableList<NotesContent>> = notesDao.getAllNotes()

    @WorkerThread
    suspend fun addNewNote(note: NotesContent) {
        notesDao.addNewNote(note)
    }

    @WorkerThread
    suspend fun editNote(note: NotesContent) {
        notesDao.editNote(note)
    }

    @WorkerThread
    suspend fun deleteNote(note: NotesContent) {
        notesDao.deleteNote(note)
    }

}