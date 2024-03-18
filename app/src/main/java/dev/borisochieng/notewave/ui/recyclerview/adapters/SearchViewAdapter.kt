package dev.borisochieng.notewave.ui.recyclerview.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.databinding.ItemSearchResultsBinding
import dev.borisochieng.notewave.ui.recyclerview.SVOnItemClickListener
import dev.borisochieng.notewave.utils.RVDiffUtil

class SearchViewAdapter(
    private val sVOnItemClickListener: SVOnItemClickListener
) :
    RecyclerView.Adapter<SearchViewAdapter.SearchViewHolder>() {

        private val asyncListDiffer = AsyncListDiffer(this, RVDiffUtil())

    inner class SearchViewHolder(private val binding: ItemSearchResultsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Note) {
            binding.apply {
                tvTitle.text = item.title
                tvContent.text = item.content
                tvDate.text = item.timeStamp

                root.setOnClickListener {
                    sVOnItemClickListener.onSVItemClick(item)
                }
            }
        }
    }

    fun setSearchResultsList(newList: List<Note>) {
        asyncListDiffer.submitList(newList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemBinding =
            ItemSearchResultsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SearchViewHolder(itemBinding)
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])

    }
}