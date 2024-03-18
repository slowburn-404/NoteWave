package dev.borisochieng.notewave.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dev.borisochieng.notewave.data.NotesRepository
import dev.borisochieng.notewave.data.models.Note
import kotlinx.coroutines.launch

class EditNoteViewModel(private val notesRepository: NotesRepository) : ViewModel() {
    fun editNote(note: Note) = viewModelScope.launch {
        notesRepository.editNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        notesRepository.deleteNote(note)
    }
    fun getNoteByID(noteId: Long): LiveData<Note?> = notesRepository.getNoteById(noteId).asLiveData()
}
class EditNoteViewModelFactory(private val notesRepository: NotesRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return EditNoteViewModel(notesRepository) as T

        }

        throw IllegalArgumentException("Unknown ViewModel class")

    }
}