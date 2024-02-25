package dev.borisochieng.notewave

import android.app.Application
import dev.borisochieng.notewave.data.local.database.NotesDataBase
import dev.borisochieng.notewave.data.NotesRepository

/*
    Since only one instance of the database
    and repository class are needed.
    We create them as members
    and only retrieved when needed
    and not constructed every time
 */
class NoteApplication: Application() {
    private val notesDataBase by lazy { NotesDataBase.getDatabase(this) }
    val notesRepository by lazy { NotesRepository(notesDataBase.notesDAO()) }

}