package dev.borisochieng.notewave.recyclerview

import dev.borisochieng.notewave.models.Note

interface RVNotesListOnItemLongClickListener {

    fun onItemLongClick(item: Note)
}