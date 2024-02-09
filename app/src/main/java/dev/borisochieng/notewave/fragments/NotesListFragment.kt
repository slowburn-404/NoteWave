package dev.borisochieng.notewave.fragments

import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.adapters.RvNotesAdapter
import dev.borisochieng.notewave.database.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentNotesListBinding
import dev.borisochieng.notewave.models.Notes
import dev.borisochieng.notewave.models.NotesContent
import dev.borisochieng.notewave.recyclerview.RVNotesListOnItemClickListener
import dev.borisochieng.notewave.viewmodels.NotesViewModel
import dev.borisochieng.notewave.viewmodels.NotesViewModelFactory

class NotesListFragment : Fragment(), RVNotesListOnItemClickListener {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvNotes: RecyclerView
    private lateinit var notesListAdapter: RvNotesAdapter
    private var notesListForRV = mutableListOf<Notes>()
    private var notesListFromViewModel = mutableListOf<NotesContent>()
    private lateinit var materialToolbarNoteList: MaterialToolbar

    private lateinit var navController: NavController
    private var actionMode: ActionMode? = null

    private val notesViewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        rvNotes = binding.rvNotes
        materialToolbarNoteList = binding.mTNotesList

        navController = findNavController()

        setUpRecyclerView()
        getNotesFromViewModel()


        binding.fabAddNote.setOnClickListener {
            navController.navigate(R.id.action_notesListFragment_to_addNoteFragment)
        }



        return binding.root
    }

    private fun setUpRecyclerView() {
        notesListAdapter = RvNotesAdapter(notesListForRV, this)
        rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvNotes.setHasFixedSize(true)
        rvNotes.adapter = notesListAdapter

    }

    private fun getNotesFromViewModel() {
        notesViewModel.getAllNotes.observe(requireActivity(), Observer { noteList ->
            noteList?.let {
                notesListForRV.clear()
                notesListFromViewModel.clear()
                it.forEach { note ->
                    notesListFromViewModel.add(
                        NotesContent(
                            noteId = note.noteId,
                            title = note.title,
                            content = note.content,
                            updatedAt = note.updatedAt
                        )
                    )
                }
                addNotesToRV(notesListFromViewModel)
            }

        })
    }

    private fun addNotesToRV(noteList: MutableList<NotesContent>) {
        noteList.forEach { note ->
            notesListForRV.add(
                Notes(
                    title = note.title,
                    content = note.content,
                    date = note.updatedAt
                )
            )
            notesListAdapter.updateList(notesListForRV)
        }
    }
    private fun showActionMode() {
        val callback = object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                val inflater: MenuInflater? = mode?.menuInflater
                inflater?.inflate(R.menu.contextual_menu_notes_list, menu)

                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.delete -> {
                        // Handle save icon press
                        deleteSelectedNote()

                        mode?.finish()

                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                actionMode = null
            }
        }

        actionMode = materialToolbarNoteList.startActionMode(callback)
    }

    private fun deleteSelectedNote() {


    }

    override fun onPause() {
        super.onPause()
        notesListForRV.clear()
    }

    override fun onItemClick(item: Notes) {

        val note = notesListFromViewModel.find { note ->
            note.title == item.title
        }
        note?.let {
            val action =
                NotesListFragmentDirections.actionNotesListFragmentToEditNoteFragment(note.noteId.toString())
            navController.navigate(action)

            Log.d("Note ID", note.noteId.toString())

        }

    }

    override fun onItemLongClick(item: Notes) {
        showActionMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}