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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentEditNoteBinding
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.data.utils.DateUtils
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModel
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModelFactory
import kotlinx.coroutines.launch


class EditNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val navArgs: EditNoteFragmentArgs by navArgs()

    private var actionMode: ActionMode? = null

    private lateinit var textInputLayoutTitle: TextInputLayout
    private lateinit var textInputEditTextTitle: TextInputEditText
    private lateinit var textViewUpdatedAt: MaterialTextView
    private lateinit var textInputLayoutEditNote: TextInputLayout
    private lateinit var textInputEditTextEditNote: TextInputEditText
    private lateinit var materialToolbarEditNote: MaterialToolbar

    private val notesViewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditNoteBinding.inflate(layoutInflater, container, false)
        initViews()

        binding.materialToolBarEditNote.setNavigationOnClickListener {
            navController.popBackStack()
        }

        addNoteToEditText()


        mutateViewsBasedOnETFocus()

        return binding.root
    }

    private fun initViews() {
        textInputLayoutTitle = binding.tILEditNoteTitle
        textInputEditTextTitle = binding.tIETEditNoteTitle
        textViewUpdatedAt = binding.dateUpdated
        textInputLayoutEditNote = binding.tILEditNote
        textInputEditTextEditNote = binding.tIETEditNote
        materialToolbarEditNote = binding.materialToolBarEditNote
        navController = findNavController()
    }

    private fun updateViewModel(note: Note) {
        notesViewModel.editNote(note)
    }

    private fun addNoteToEditText() {
        val noteIdFromNotesListFragment = navArgs.noteId

        lifecycleScope.launch {
            notesViewModel.filterNotesByID(noteIdFromNotesListFragment).observe(requireActivity(), Observer {selectedNote ->
                selectedNote?.let {
                    Log.d("Selected Note", selectedNote.toString())
                    textInputEditTextTitle.setText(selectedNote.title)
                    textInputLayoutTitle.hint = null
                    textInputEditTextEditNote.setText(selectedNote.content)
                    textInputLayoutEditNote.hint = null

                }
            })
        }

    }

    private fun mutateViewsBasedOnETFocus() {
        textInputEditTextTitle.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutTitle.hint = null

                showActionMode()
            }
            requireActivity().invalidateOptionsMenu()

        }

        textInputEditTextEditNote.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutEditNote.hint = null

                showActionMode()
            }
        }
    }

    private fun showActionMode() {
        val callback = object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                val inflater: MenuInflater? = mode?.menuInflater
                inflater?.inflate(R.menu.contextual_menu_save_note, menu)

                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.save -> {
                        // Handle save icon press
                        val note = prepareDataForViewModel()
                        Log.d("Edited Note", note.toString())
                        updateViewModel(note)
                        Snackbar.make(binding.root, "Note saved.", Snackbar.LENGTH_SHORT).show()
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

        actionMode = materialToolbarEditNote.startActionMode(callback)
    }

    private fun prepareDataForViewModel(): Note {
        val noteId = navArgs.noteId
        val title = textInputEditTextTitle.text?.trim().toString()
        val content = textInputEditTextEditNote.text?.trim().toString()
        val date = DateUtils.getCurrentDate()

        return Note(noteId, title, content, date)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}