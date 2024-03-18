package dev.borisochieng.notewave.ui.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.addCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import dev.borisochieng.notewave.NoteApplication
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.ui.recyclerview.adapters.RvNotesAdapter
import dev.borisochieng.notewave.databinding.FragmentNotesListBinding
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.ui.recyclerview.RVNotesItemDetailsLookup
import dev.borisochieng.notewave.ui.recyclerview.RVNotesItemKeyProvider
import dev.borisochieng.notewave.ui.recyclerview.OnItemClickListener
import dev.borisochieng.notewave.ui.recyclerview.OnItemLongClickListener
import dev.borisochieng.notewave.ui.recyclerview.SVOnItemClickListener
import dev.borisochieng.notewave.ui.recyclerview.adapters.SearchViewAdapter
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModel
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModelFactory
import java.util.Locale

class NotesListFragment : Fragment(), OnItemClickListener,
    OnItemLongClickListener, SVOnItemClickListener {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvNotesList: RecyclerView
    private lateinit var notesListAdapter: RvNotesAdapter
    private var notesListFromViewModel = mutableSetOf<Note>()
    private var selectedNotesList = mutableListOf<Note>()
    private var searchResultsList = listOf<Note>()
    private lateinit var selectionTracker: SelectionTracker<Long>
    private lateinit var searchBarNotesList: SearchBar
    private lateinit var rvSearchView: RecyclerView
    private lateinit var searchViewAdapter: SearchViewAdapter
    private lateinit var searchViewNotesList: SearchView
    private lateinit var appBar: AppBarLayout

    private lateinit var navController: NavController
    private var actionMode: ActionMode? = null

    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        setInsetsForView(binding.fabAddNote)
        setInsetsForView(binding.rvNotes)
        //Init views
        binding.apply {
            rvNotesList = rvNotes
            searchBarNotesList = searchBar
            appBar = aBNotesList
            rvSearchView = rvSearchResults
            searchViewNotesList = searchView
        }
        navController = findNavController()


        initRecyclerView()
        initSelectionTracker()
        getNotesFromViewModel()
        initSearchViewRecyclerView()
        listenForTextChanges()
        //collapseSearchViewOnBackPress()

        binding.fabAddNote.setOnClickListener {
            navController.navigate(R.id.action_notesListFragment_to_addNoteFragment)
        }

        //restore tracker if needed
        savedInstanceState?.let {
            selectionTracker.onRestoreInstanceState(it)
        }

        //get text from search and add to search bar
        searchViewNotesList.editText.setOnEditorActionListener { _, _, _ ->
            searchBarNotesList.setText(searchViewNotesList.text.toString())
            false
        }

        return binding.root
    }

    private fun initRecyclerView() {
        notesListAdapter = RvNotesAdapter(this, this)
        rvNotesList.apply {
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = notesListAdapter
        }
    }

    private fun initSearchViewRecyclerView() {
        searchViewAdapter = SearchViewAdapter(this)
        rvSearchView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = searchViewAdapter
        }
    }
    /*private fun collapseSearchViewOnBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (searchBarNotesList.collapse(appBar)) {
                searchViewNotesList.hide()
            } else {
                requireActivity().finish()
            }
        }
    }*/

    private fun getNotesFromViewModel() {
        notesViewModel.allNotes.observe(viewLifecycleOwner, Observer { noteList ->
            noteList?.let {
                notesListFromViewModel.addAll(it)
                notesListAdapter.updateList(notesListFromViewModel.toMutableList())
            }
        })
    }

    private fun initSelectionTracker() {
        selectionTracker = SelectionTracker.Builder(
            "noteId",
            rvNotesList,
            RVNotesItemKeyProvider(notesListAdapter),
            RVNotesItemDetailsLookup(rvNotesList),
            StorageStrategy.createLongStorage()
        ).build()
        notesListAdapter.selectionTracker = selectionTracker

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
                selectionTracker.clearSelection()
            }
        }

        actionMode = appBar.startActionMode(callback)

    }

    private fun updateActionModeTitle() {
        actionMode?.title = resources.getQuantityString(
            R.plurals.selected_items,
            selectedNotesList.size,
            selectedNotesList.size
        )
    }

    private fun showDialog() {
        val selectedNotesListCopy = selectedNotesList.toMutableList()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_selected_items))
            .setMessage(getString(R.string.delete_item_message))
            .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
                deleteSelectedNotes(selectedNotesListCopy)
                dialog.dismiss()
            }
            .setNegativeButton(resources.getString(R.string.no)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateSelectedNotesList() {
        selectedNotesList.clear()
        val selectedNotesIDs = selectionTracker.selection.toMutableList()
        selectedNotesList.addAll(selectedNotesIDs.mapNotNull { id ->
            notesListFromViewModel.find { note ->
                id == note.noteId
            }
        })
    }

    private fun deleteSelectedNotes(selectedNotes: MutableList<Note>) {
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

    private fun setInsetsForView(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updateLayoutParams<MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
            }

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun filterNotesList(query: String) {
        val lowerCaseQuery = query.lowercase(Locale.getDefault())
        searchResultsList = if (query.isEmpty()) {
            emptyList()
        } else {
            notesListFromViewModel.filter { note ->
                note.title.lowercase(Locale.getDefault()).contains(lowerCaseQuery) || note.content.lowercase(Locale.getDefault()).contains(lowerCaseQuery)
             }
        }
        searchViewAdapter.setSearchResultsList(searchResultsList)
    }

    private fun listenForTextChanges() {
        searchViewNotesList.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterNotesList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    override fun onItemClick(item: Note) {
        val action =
            NotesListFragmentDirections.actionNotesListFragmentToEditNoteFragment(
                item.noteId
            )
        navController.navigate(action)

        selectionTracker.clearSelection()
    }

    override fun onSVItemClick(item: Note) {
        val action =
            NotesListFragmentDirections.actionNotesListFragmentToEditNoteFragment(
                item.noteId
            )
        navController.navigate(action)

    }

    override fun onItemLongClick(item: Note) {
        showActionMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}