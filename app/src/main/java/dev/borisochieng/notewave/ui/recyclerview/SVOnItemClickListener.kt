package dev.borisochieng.notewave.ui.recyclerview

import dev.borisochieng.notewave.data.models.Note

interface SVOnItemClickListener {
    fun onSVItemClick(item: Note)
}