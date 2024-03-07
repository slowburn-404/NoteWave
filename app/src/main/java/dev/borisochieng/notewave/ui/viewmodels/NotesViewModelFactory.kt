package dev.borisochieng.notewave.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.borisochieng.notewave.data.NotesRepository

class NotesViewModelFactory(private val notesRepository: NotesRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotesViewModel(notesRepository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }
}