package dev.borisochieng.notewave.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.adapters.RvNotesAdapter
import dev.borisochieng.notewave.database.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentNotesListBinding
import dev.borisochieng.notewave.models.Notes
import dev.borisochieng.notewave.recyclerview.RVNotesListOnItemClickListener
import dev.borisochieng.notewave.viewmodels.NotesViewModel
import dev.borisochieng.notewave.viewmodels.NotesViewModelFactory

class NotesListFragment : Fragment(), RVNotesListOnItemClickListener {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvNotes: RecyclerView
    private lateinit var notesListAdapter: RvNotesAdapter
    private var notesList = mutableListOf<Notes>()

    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        rvNotes = binding.rvNotes

        setUpRecyclerView()
        getNotesFromViewModel()


        binding.fabAddNote.setOnClickListener {
            findNavController().navigate(R.id.action_notesListFragment_to_addNoteFragment)
        }



        return binding.root
    }

    private fun setUpRecyclerView() {
        notesListAdapter = RvNotesAdapter(notesList, this)
        rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvNotes.setHasFixedSize(true)
        rvNotes.adapter = notesListAdapter

    }

    private fun getNotesFromViewModel() {
        notesList.clear()
        notesViewModel.getAllNotes.observe(requireActivity(), Observer { note ->
            note?.forEach {
                notesList.add(Notes(it.title, it.content, it.updatedAt))
                notesListAdapter.updateList(notesList)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        notesList.clear()
    }

    override fun onItemClick(item: Notes) {
        findNavController().navigate(R.id.action_notesListFragment_to_editNoteFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}