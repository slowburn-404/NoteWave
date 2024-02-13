package dev.borisochieng.notewave.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import dev.borisochieng.notewave.recyclerview.RVNotesListOnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.databinding.ItemNotesBinding
import dev.borisochieng.notewave.models.Notes

class RvNotesAdapter(
    private var notesList: MutableList<Notes> = mutableListOf(),
    private val onItemClickListener: RVNotesListOnItemClickListener,
    private var multiSelectMode: Boolean = false,
    //private val selectedItemsList: MutableSet<Notes> = mutableSetOf()
) : RecyclerView.Adapter<RvNotesAdapter.RvNotesViewHolder>() {

    inner class RvNotesViewHolder(binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val tvTitle: MaterialTextView = binding.tvTitle
        private val tvContent: TextView = binding.tvContent
        private val tvDate: TextView = binding.tvDate
        val notesCard: MaterialCardView = binding.notesCard


        fun bind(notes: Notes) {
            tvTitle.text = notes.title
            tvContent.text = notes.content
            tvDate.text = notes.date
        }

    }

    fun updateList(newList: MutableList<Notes>) {
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
        holder.bind(item)
        holder.itemView.setOnClickListener {
            if(multiSelectMode) {
                holder.notesCard.isChecked = !holder.notesCard.isChecked
            } else {
                onItemClickListener.onItemClick(item)
            }

        }

        holder.itemView.setOnLongClickListener {
            multiSelectMode = true
            holder.notesCard.isChecked = !holder.notesCard.isChecked
            onItemClickListener.onItemLongClick(item)


            true
        }

    }

    override fun getItemCount() = notesList.size

}