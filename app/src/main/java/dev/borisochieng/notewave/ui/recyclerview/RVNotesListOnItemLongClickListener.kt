package dev.borisochieng.notewave.ui.recyclerview

import dev.borisochieng.notewave.data.models.Note

interface RVNotesListOnItemLongClickListener {

    fun onItemLongClick(item: Note)
}