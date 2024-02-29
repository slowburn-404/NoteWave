package dev.borisochieng.notewave.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.data.NotesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class NotesViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    val allNotes: LiveData<MutableList<Note>> = notesRepository.getAllNotes.asLiveData()

    fun addNewNote(note: Note) = viewModelScope.launch {
        notesRepository.addNewNote(note)
    }

    fun editNote(note: Note) = viewModelScope.launch {
        notesRepository.editNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        notesRepository.deleteNote(note)
    }

    suspend fun filterNotesByID(noteId: Long): LiveData<Note?> = notesRepository.getNoteById(noteId).asLiveData()

}