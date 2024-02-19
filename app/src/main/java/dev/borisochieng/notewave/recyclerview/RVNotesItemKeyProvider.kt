package dev.borisochieng.notewave.recyclerview

import androidx.recyclerview.selection.ItemKeyProvider
import dev.borisochieng.notewave.adapters.RvNotesAdapter

class RVNotesItemKeyProvider(private val adapter: RvNotesAdapter) : ItemKeyProvider<Long>(
    SCOPE_MAPPED
) {

    override fun getKey(position: Int): Long {
        return adapter.getItem(position).noteId

    }

    override fun getPosition(key: Long): Int {
        return adapter.getPosition(key)
    }
}