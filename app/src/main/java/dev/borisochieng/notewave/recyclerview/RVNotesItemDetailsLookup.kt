package dev.borisochieng.notewave.recyclerview

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import dev.borisochieng.notewave.adapters.RvNotesAdapter

class RVNotesItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)

        view?.let {
                return (recyclerView.getChildViewHolder(it) as RvNotesAdapter.RvNotesViewHolder).getItemDetails()
        }
        return null
    }
}