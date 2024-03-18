package dev.borisochieng.notewave.ui.recyclerview

import dev.borisochieng.notewave.data.models.Note

interface OnItemClickListener {
    fun onItemClick(item: Note)
}