package dev.borisochieng.notewave.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.adapters.RvNotesAdapter
import dev.borisochieng.notewave.databinding.FragmentNotesListBinding
import dev.borisochieng.notewave.models.Notes
import dev.borisochieng.notewave.recyclerview.RVNotesListOnItemClickListener

class NotesListFragment : Fragment(), RVNotesListOnItemClickListener {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvNotes: RecyclerView
    private lateinit var notesListAdapter: RvNotesAdapter
    private var notesList = ArrayList<Notes>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        rvNotes = binding.rvNotes

        for (i in 1..2) {
            notesList.add(
                Notes(
                    "Activity Lifecycle",
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean nec orci suscipit, tristique orci sed, tempor dui. Sed luctus, sapien vitae ullamcorper lacinia, ligula odio dignissim velit, eget aliquet turpis tortor nec lorem. Sed vel vehicula massa. Proin nisi turpis, lacinia at semper vel, tincidunt in nibh. Cras id mi fermentum, fringilla velit non, congue tortor. Vestibulum lacinia pretium enim, et ornare elit consectetur non. Mauris finibus bibendum erat, id condimentum nisl congue ut.",
                    "18-01-24"
                )
            )
            notesList.add(
                Notes(
                    "Jetpack Compose",
                    "Fusce enim risus, maximus eu rutrum elementum, fringilla at tellus. Cras nulla justo, tristique vitae mauris quis, scelerisque iaculis est.",
                    "18/01/24"
                )
            )
            notesList.add(
                Notes(
                    "Navigation",
                    "Phasellus tempor tincidunt arcu, a ullamcorper mauris lobortis ac. Donec scelerisque dictum odio sit amet rhoncus. Phasellus scelerisque interdum neque vel sagittis.",
                    "18 January 2024"
                )
            )
            notesList.add(
                Notes(
                    "Clean Architecture",
                    "Phasellus purus nisl, sagittis eget enim ut, mollis pulvinar felis. Nunc quis luctus leo. Aenean eleifend fermentum risus nec ultrices. Fusce sodales, urna et mattis tristique, lorem risus placerat risus, eu blandit nibh risus nec nulla. Donec nunc lorem, sodales ac elit eu, elementum tristique erat. Phasellus suscipit sem eget velit lacinia interdum. Donec gravida sapien dolor, vitae accumsan nisl molestie ac. Sed faucibus tortor euismod risus dignissim, nec luctus turpis consectetur. Morbi sagittis magna ex, in rhoncus ex commodo non. Suspendisse accumsan turpis ligula. Aenean sed suscipit erat. Etiam ut quam non nunc viverra mattis nec vel tortor.",
                    "18/01/24"
                )
            )
            notesList.add(
                Notes(
                    "Services",
                    "Aliquam tellus leo, efficitur varius interdum vel, ultricies at augue.",
                    "18/01/24"
                )
            )
        }
        setUpRecyclerView()

        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_notesListFragment_to_editNoteFragment)
        }



        return binding.root
    }

    private fun setUpRecyclerView() {
        notesListAdapter = RvNotesAdapter(notesList, this)
        rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvNotes.setHasFixedSize(true)
        rvNotes.adapter = notesListAdapter

    }

    override fun onItemClick(item: Notes) {
        findNavController().navigate(R.id.action_notesListFragment_to_editNoteFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}