package dev.borisochieng.notewave.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import dev.borisochieng.notewave.recyclerview.RVNotesListOnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.databinding.ItemNotesBinding
import dev.borisochieng.notewave.models.Note
import dev.borisochieng.notewave.recyclerview.RVNotesListOnItemLongClickListener

class RvNotesAdapter(
    private var notesList: MutableList<Note> = mutableListOf(),
    private val onItemClickListener: RVNotesListOnItemClickListener,
    private val onItemLongClickListener: RVNotesListOnItemLongClickListener
) : RecyclerView.Adapter<RvNotesAdapter.RvNotesViewHolder>() {
    lateinit var selectionTracker: SelectionTracker<Long>

    inner class RvNotesViewHolder(binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val tvTitle: MaterialTextView = binding.tvTitle
        private val tvContent: TextView = binding.tvContent
        private val tvDate: TextView = binding.tvDate
        val notesCard: MaterialCardView = binding.notesCard


        fun bind(note: Note, isSelected: Boolean = false) {
            tvTitle.text = note.title
            tvContent.text = note.content
            tvDate.text = note.updatedAt

            //update selection state of the recycler view
            itemView.isActivated = isSelected
            notesCard.isChecked = isSelected

            itemView.tag = note.noteId
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long {
                    return (itemView.tag as? Long) ?: RecyclerView.NO_ID
                }
            }

    }

    fun updateList(newList: MutableList<Note>) {
        notesList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RvNotesAdapter.RvNotesViewHolder {
        val itemBinding =
            ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RvNotesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RvNotesViewHolder, position: Int) {

        val item = notesList[position]

        //bind item data and selection state
        holder.bind(item)

        holder.itemView.setOnClickListener {
            if (selectionTracker.isSelected(item.noteId)) {
                selectionTracker.deselect(item.noteId)
            } else {
                if (selectionTracker.hasSelection()) {
                    return@setOnClickListener
                }
                selectionTracker.select(item.noteId)
            }
            onItemClickListener.onItemClick(item)

        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener.onItemLongClick(item)

            true
        }

        holder.notesCard.isChecked = selectionTracker.isSelected(item.noteId)

    }

    fun getItem(position: Int) = notesList[position]
    fun getPosition(key: Long) = notesList.indexOfFirst { it.noteId == key }

    override fun getItemId(position: Int): Long {
        return notesList[position].noteId
    }

    override fun getItemCount() = notesList.size

}