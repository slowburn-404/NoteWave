package dev.borisochieng.notewave.ui.recyclerview

import androidx.recyclerview.selection.ItemKeyProvider
import dev.borisochieng.notewave.ui.recyclerview.adapters.RvNotesAdapter

class RVNotesItemKeyProvider(private val adapter: RvNotesAdapter) : ItemKeyProvider<Long>(
    SCOPE_MAPPED
) {

    override fun getKey(position: Int): Long = adapter.getItem(position).noteId

    override fun getPosition(key: Long): Int = adapter.getPosition(key)

}