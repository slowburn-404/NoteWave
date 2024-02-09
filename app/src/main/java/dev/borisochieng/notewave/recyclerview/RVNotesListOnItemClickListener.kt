package dev.borisochieng.notewave.recyclerview

import dev.borisochieng.notewave.models.Notes

interface RVNotesListOnItemClickListener {
    fun onItemClick(item: Notes)

    fun onItemLongClick(item: Notes)
}