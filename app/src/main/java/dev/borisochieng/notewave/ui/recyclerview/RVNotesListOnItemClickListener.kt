package dev.borisochieng.notewave.ui.recyclerview

import dev.borisochieng.notewave.data.models.Note

interface RVNotesListOnItemClickListener {
    fun onItemClick(item: Note)
}