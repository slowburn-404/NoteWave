package dev.borisochieng.notewave.fragments

import android.os.Bundle
import android.os.NetworkOnMainThreadException
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
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.adapters.RvNotesAdapter
import dev.borisochieng.notewave.database.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentNotesListBinding
import dev.borisochieng.notewave.models.Note
import dev.borisochieng.notewave.recyclerview.RVNotesItemDetailsLookup
import dev.borisochieng.notewave.recyclerview.RVNotesItemKeyProvider
import dev.borisochieng.notewave.recyclerview.RVNotesListOnItemClickListener
import dev.borisochieng.notewave.viewmodels.NotesViewModel
import dev.borisochieng.notewave.viewmodels.NotesViewModelFactory

class NotesListFragment : Fragment(), RVNotesListOnItemClickListener {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvNotes: RecyclerView
    private lateinit var notesListAdapter: RvNotesAdapter
    private var notesListForRV = mutableListOf<Note>()
    private var notesListFromViewModel = mutableListOf<Note>()
    private var selectedNotesIDList = mutableListOf<Long>()
    private var selectedNotesList = mutableListOf<Note>()
    private lateinit var materialToolbarNoteList: MaterialToolbar
    private lateinit var selectionTracker: SelectionTracker<Long>

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

        //restore tracker if needed
        savedInstanceState?.let {
            selectionTracker.onRestoreInstanceState(it)
        }


        return binding.root
    }

    private fun setUpRecyclerView() {


        notesListAdapter = RvNotesAdapter(notesListForRV, this, false)
        rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvNotes.setHasFixedSize(true)
        rvNotes.adapter = notesListAdapter

        selectionTracker = SelectionTracker.Builder(
            "noteId",
            rvNotes,
            RVNotesItemKeyProvider(notesListAdapter),
            RVNotesItemDetailsLookup(rvNotes),
            StorageStrategy.createLongStorage()
        ).build()
        notesListAdapter.selectionTracker = selectionTracker

    }

    private fun getNotesFromViewModel() {
        notesViewModel.getAllNotes.observe(requireActivity(), Observer { noteList ->
            noteList?.let {
                notesListForRV.clear()
                notesListFromViewModel.clear()
                it.forEach { note ->
                    notesListFromViewModel.add(
                        Note(
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

    private fun addNotesToRV(noteList: MutableList<Note>) {
        noteList.forEach { note ->
            notesListForRV.add(
                Note(
                    noteId = note.noteId,
                    title = note.title,
                    content = note.content,
                    updatedAt = note.updatedAt
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
                        // Handle delete icon press
                        showDialog()

                        mode?.finish()

                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                actionMode = null
                selectedNotesIDList.clear()
                rvNotes.adapter?.notifyDataSetChanged()

            }
        }

        actionMode = materialToolbarNoteList.startActionMode(callback)
        actionMode?.title = selectedNotesIDList.size.toString()
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_selected_items))
            .setMessage(getString(R.string.delete_item_message))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                deleteNote()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun deleteNote() {
        val selectedNotes =  getSelectedNotesFromViewModel()
        selectedNotes.forEach { selectedNote ->
            if (selectedNote != null) {
                notesViewModel.deleteNote(selectedNote)
                actionMode?.finish()
                Snackbar.make(binding.root, "Notes deleted", Snackbar.LENGTH_SHORT).show()
            }
            else {
                Snackbar.make(binding.root, "No notes selected", Snackbar.LENGTH_SHORT).show()
                actionMode?.finish()
            }
        }
    }

    private fun getSelectedNotes(): MutableList<Long> {
        selectionTracker.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    selectedNotesIDList = selectionTracker.selection.toMutableList()
                }

            }
        )
        return selectedNotesIDList
    }
    private fun getSelectedNotesFromViewModel(): MutableList<Note?> {
        val selectedNotesIDs = getSelectedNotes()
        val selectedNotesList = selectedNotesIDs.mapTo(mutableListOf()) { noteId ->
            notesListFromViewModel.find {note ->
                noteId == note.noteId
            }
        }
        return selectedNotesList
    }

    override fun onPause() {
        super.onPause()
        notesListForRV.clear()
    }

    override fun onItemClick(item: Note) {

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

    override fun onItemLongClick(item: Note) {
        showActionMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}