package dev.borisochieng.notewave.utils

import androidx.recyclerview.widget.DiffUtil
import dev.borisochieng.notewave.data.models.Note

class RVDiffUtil(
) : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean =
        oldItem.noteId == newItem.noteId


    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean =
        oldItem == newItem
}