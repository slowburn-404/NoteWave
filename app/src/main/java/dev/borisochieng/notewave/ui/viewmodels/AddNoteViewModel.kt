package dev.borisochieng.notewave.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.borisochieng.notewave.data.NotesRepository
import dev.borisochieng.notewave.data.models.Note
import kotlinx.coroutines.launch

class AddNoteViewModel(private val notesRepository: NotesRepository) : ViewModel() {

    fun addNewNote(note: Note) = viewModelScope.launch {
        notesRepository.addNewNote(note)
    }
}

class AddNoteViewModelFactory(private val notesRepository: NotesRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddNoteViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return AddNoteViewModel(notesRepository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }
}