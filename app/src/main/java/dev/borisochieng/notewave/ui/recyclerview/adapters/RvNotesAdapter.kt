package dev.borisochieng.notewave.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.AsyncListDiffer
import dev.borisochieng.notewave.ui.recyclerview.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.databinding.ItemNotesBinding
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.ui.recyclerview.OnItemLongClickListener
import dev.borisochieng.notewave.utils.RVDiffUtil

class RvNotesAdapter(
    private val onItemClickListener: OnItemClickListener,
    private val onItemLongClickListener: OnItemLongClickListener
) : RecyclerView.Adapter<RvNotesAdapter.RvNotesViewHolder>() {
    lateinit var selectionTracker: SelectionTracker<Long>

    inner class RvNotesViewHolder(private val binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val notesCard: MaterialCardView = binding.notesCard


        fun bind(item: Note, isSelected: Boolean = false) {
            binding.apply {
                tvTitle.text = item.title
                tvContent.text = item.content
                tvDate.text = item.timeStamp

                //update selection state of the recycler view
                itemView.isActivated = isSelected
                notesCard.isChecked = isSelected

                //tag each item by its unique identifier
                itemView.tag = item.noteId

                root.setOnClickListener {
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
                root.setOnLongClickListener {
                    onItemLongClickListener.onItemLongClick(item)

                    true
                }

                notesCard.isChecked = selectionTracker.isSelected(item.noteId)
            }


        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long {
                    return (itemView.tag as? Long) ?: RecyclerView.NO_ID
                }
            }
    }

    private val asyncListDiffer = AsyncListDiffer(this, RVDiffUtil())

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
    }

    fun getItem(position: Int): Note = asyncListDiffer.currentList[position]

    fun getPosition(key: Long): Int = asyncListDiffer.currentList.indexOfFirst { it.noteId == key }

    override fun getItemId(position: Int): Long = asyncListDiffer.currentList[position].noteId

    override fun getItemCount() = asyncListDiffer.currentList.size

}