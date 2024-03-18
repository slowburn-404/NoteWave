package dev.borisochieng.notewave.ui.recyclerview

import dev.borisochieng.notewave.data.models.Note

interface OnItemLongClickListener {

    fun onItemLongClick(item: Note)
}