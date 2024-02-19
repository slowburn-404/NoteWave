package dev.borisochieng.notewave.recyclerview

import dev.borisochieng.notewave.models.Note

interface RVNotesListOnItemClickListener {
    fun onItemClick(item: Note)

    fun onItemLongClick(item: Note)
}