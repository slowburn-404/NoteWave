package dev.borisochieng.notewave.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.databinding.ItemNotesBinding
import dev.borisochieng.notewave.ui.recyclerview.SVOnItemClickListener

class SearchViewAdapter(
    private val onItemClickListener: SVOnItemClickListener,
    private var searchResultsList: List<Note>
) :
    RecyclerView.Adapter<SearchViewAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(binding: ItemNotesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val title = binding.tvTitle
        private val content = binding.tvContent
        private val timestamp = binding.tvDate

        fun bind(item: Note) {
            title.text = item.title
            content.text = item.content
            timestamp.text = item.timeStamp

        }


    }
    fun setSearchResultsList(newList: List<Note>) {
        this.searchResultsList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemBinding =
            ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SearchViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = searchResultsList.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = searchResultsList[position]

        holder.bind(item)

        holder.itemView.setOnClickListener {
            onItemClickListener.onSVItemClick(item)
        }
    }
}