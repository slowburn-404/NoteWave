package dev.borisochieng.notewave.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import dev.borisochieng.notewave.recyclerview.RVNotesListOnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import dev.borisochieng.notewave.databinding.ItemNotesBinding
import dev.borisochieng.notewave.models.Notes
import dev.borisochieng.notewave.models.NotesContent

class RvNotesAdapter(
    private var notesList: MutableList<Notes> = mutableListOf(),
    private val onItemClickListener: RVNotesListOnItemClickListener
) : RecyclerView.Adapter<RvNotesAdapter.RvNotesViewHolder>() {

    inner class RvNotesViewHolder(private val binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notes: Notes) {
            binding.tvTitle.text = notes.title
            binding.tvContent.text = notes.content
            binding.tvDate.text = notes.date
        }

    }

    fun updateList(newList: MutableList<Notes>) {
        notesList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): RvNotesAdapter.RvNotesViewHolder {
        val itemBinding = ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent ,false)

        return RvNotesViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: RvNotesViewHolder, position: Int) {

        val item = notesList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(item)
        }

        holder.itemView.setOnLongClickListener {
            onItemClickListener.onItemLongClick(item)
            true
        }

    }

    override fun getItemCount() = notesList.size

}