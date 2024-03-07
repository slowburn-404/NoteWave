package dev.borisochieng.notewave.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import dev.borisochieng.notewave.ui.recyclerview.RVNotesListOnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.databinding.ItemNotesBinding
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.ui.recyclerview.RVNotesListOnItemLongClickListener

class RvNotesAdapter(
    //private var notesList: MutableList<Note> = mutableListOf(),
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
            tvDate.text = note.timeStamp

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
    private val diffUtil = object: DiffUtil.ItemCallback<Note>() {
        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.noteId == newItem.noteId
        }
    }

    private val asyncListDiffer =  AsyncListDiffer(this, diffUtil)

    fun updateList(newList: MutableList<Note>) {
        asyncListDiffer.submitList(newList)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RvNotesViewHolder {
        val itemBinding =
            ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RvNotesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RvNotesViewHolder, position: Int) {

        val item = asyncListDiffer.currentList[position]

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
                onItemClickListener.onItemClick(item)
            }


        }

        holder.itemView.setOnLongClickListener {
            onItemLongClickListener.onItemLongClick(item)

            true
        }

        holder.notesCard.isChecked = selectionTracker.isSelected(item.noteId)

    }

    fun getItem(position: Int) = asyncListDiffer.currentList[position]
    fun getPosition(key: Long) = asyncListDiffer.currentList.indexOfFirst { it.noteId == key }

    override fun getItemId(position: Int): Long {
        return asyncListDiffer.currentList[position].noteId
    }

    override fun getItemCount() = asyncListDiffer.currentList.size

}