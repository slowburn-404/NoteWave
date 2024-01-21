package dev.borisochieng.notewave.database

import android.app.Application
import android.content.Context
import dev.borisochieng.notewave.repositories.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/*
    Since only one instance of the database
    and repository class are needed.
    We create them as members
    and only retrieved when needed
    and not constructed every time
 */
class NoteApplication: Application() {


    val notesDataBase by lazy { NotesDataBase.getDatabase(this) }
    val notesRepository by lazy { NotesRepository(notesDataBase.notesDAO()) }
}