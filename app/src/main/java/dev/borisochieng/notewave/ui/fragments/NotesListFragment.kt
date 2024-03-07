package dev.borisochieng.notewave.ui.fragments

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
import dev.borisochieng.notewave.ui.recyclerview.adapters.RvNotesAdapter
import dev.borisochieng.notewave.databinding.FragmentNotesListBinding
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.ui.activities.MainActivity
import dev.borisochieng.notewave.ui.recyclerview.RVNotesItemDetailsLookup
import dev.borisochieng.notewave.ui.recyclerview.RVNotesItemKeyProvider
import dev.borisochieng.notewave.ui.recyclerview.RVNotesListOnItemClickListener
import dev.borisochieng.notewave.ui.recyclerview.RVNotesListOnItemLongClickListener
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModel

class NotesListFragment : Fragment(), RVNotesListOnItemClickListener,
    RVNotesListOnItemLongClickListener {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvNotes: RecyclerView
    private lateinit var notesListAdapter: RvNotesAdapter
    private var notesListFromViewModel = mutableSetOf<Note>()
    private var selectedNotesList =  mutableListOf<Note>()
    private lateinit var materialToolbarNoteList: MaterialToolbar
    private lateinit var selectionTracker: SelectionTracker<Long>

    private lateinit var navController: NavController
    private var actionMode: ActionMode? = null

    private lateinit var notesViewModel: NotesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)

        //Init views
        rvNotes = binding.rvNotes
        materialToolbarNoteList = binding.mTNotesList
        navController = findNavController()

        notesViewModel = (activity as MainActivity).notesViewModel

        setUpRecyclerView()
        setUpSelectionTracker()
        getNotesFromViewModel()

        binding.fabAddNote.apply {
            setOnClickListener {
                navController.navigate(R.id.action_notesListFragment_to_addNoteFragment)
            }
        }

        val selectionObserver = object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                updateSelectedNotesList()
                updateActionModeTitle()

                if (!selectionTracker.hasSelection()) {
                    actionMode?.finish()

                }
                binding.fabAddNote.isEnabled = !selectionTracker.hasSelection()

            }
        }
        selectionTracker.addObserver(selectionObserver)

        //restore tracker if needed
        savedInstanceState?.let {
            selectionTracker.onRestoreInstanceState(it)
        }



        return binding.root
    }

    private fun setUpRecyclerView() {
        notesListAdapter = RvNotesAdapter(this, this)
        rvNotes.adapter = notesListAdapter
        rvNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvNotes.setHasFixedSize(true)

    }

    private fun setUpSelectionTracker() {
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
        notesViewModel.allNotes.observe(requireActivity(), Observer { noteList ->
            noteList?.let {notes ->
                notesListFromViewModel.addAll(notes)
                notesListAdapter.updateList(notesListFromViewModel.toMutableList())
            }

        })
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
                        showDialog()
                        mode?.finish()

                        true
                    }
                    R.id.select_all -> {
                        /*
                        If all items are selected, clear selection else
                        select all
                        */
                        if (selectedNotesList.size == notesListFromViewModel.size) {
                            selectionTracker.clearSelection()

                        } else {
                            notesListFromViewModel.forEach { note ->
                                selectionTracker.select(note.noteId)

                            }

                        }

                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                actionMode = null
                selectedNotesList.clear()
            }
        }

        actionMode = materialToolbarNoteList.startActionMode(callback)
    }

    private fun updateActionModeTitle() {
        actionMode?.title = resources.getQuantityString(
            R.plurals.selected_items,
            selectedNotesList.size,
            selectedNotesList.size
        )
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_selected_items))
            .setMessage(getString(R.string.delete_item_message))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                deleteSelectedNotes()
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
                selectionTracker.clearSelection()
                actionMode?.finish()
            }.show()
    }

    private fun deleteSelectedNotes() {
        val selectedNotes = getSelectedNotesFromViewModel()

        if (selectedNotes.isNotEmpty()) {
            selectedNotes.forEach { selectedNote ->
                    notesViewModel.deleteNote(selectedNote)                
            }
            notesListFromViewModel.removeAll(selectedNotes.toSet())
            notesListAdapter.updateList(notesListFromViewModel.toMutableList())
            actionMode?.finish()
            Snackbar
                .make(
                    binding.root,
                    resources.getQuantityString(R.plurals.deleted_notes, selectedNotes.size),
                    Snackbar.LENGTH_SHORT
                )
                .show()
        }
    }

    private fun getSelectedNotes(): MutableList<Long> = selectionTracker.selection.toMutableList()


    private fun getSelectedNotesFromViewModel(): MutableList<Note> {
        val selectedNotesIDs = getSelectedNotes()
        return selectedNotesIDs.mapNotNull { noteID ->
            notesListFromViewModel.find { note ->
                noteID == note.noteId
            }
        } as MutableList<Note>
    }

    private fun updateSelectedNotesList() {
        selectedNotesList.clear()
        val selectedNotesIDs = getSelectedNotes()
        selectedNotesList.addAll(selectedNotesIDs.mapNotNull { id ->
            notesListFromViewModel.find { note ->
                id == note.noteId
            }
        })
    }

    override fun onItemClick(item: Note) {
        val note = getClickedNote(item)
        note?.let {
            val action =
                NotesListFragmentDirections.actionNotesListFragmentToEditNoteFragment(
                    note.noteId
                )
            navController.navigate(action)

            Log.d("Note ID", note.noteId.toString())
        }

        selectionTracker.clearSelection()

    }

    private fun getClickedNote(item: Note): Note? {
        return notesListFromViewModel.find { note ->
            note.noteId == item.noteId
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