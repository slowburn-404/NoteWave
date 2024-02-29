package dev.borisochieng.notewave.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.ActionMode.Callback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ActionMode
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentAddNoteBinding
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModel
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModelFactory
import dev.borisochieng.notewave.data.utils.DateUtils

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var textInputLayoutAddNote: TextInputLayout
    private lateinit var textInputEditTextAddNote: TextInputEditText
    private lateinit var materialToolbarAddNote: MaterialToolbar
    private lateinit var textViewDateUpdate: MaterialTextView
    private lateinit var textInputLayoutTitle: TextInputLayout
    private lateinit var textInputEditTextTitle: TextInputEditText

    private var actionMode: ActionMode? = null

    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        initViews()
        mutateViewsBasedOnETFocus()

        materialToolbarAddNote.setNavigationOnClickListener {
            navController.popBackStack()
        }
        textViewDateUpdate.text = DateUtils.getCurrentDate()


        return binding.root
    }

    private fun initViews() {
        navController = findNavController()
        textInputLayoutAddNote = binding.tILAddNote
        textInputEditTextAddNote = binding.tIETAddNote
        materialToolbarAddNote = binding.toolBarAddNote
        textViewDateUpdate = binding.dateUpdated
        textInputLayoutTitle = binding.tILAddNoteTitle
        textInputEditTextTitle = binding.tIETAddNoteTitle
    }

    private fun mutateViewsBasedOnETFocus() {
        textInputEditTextAddNote.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutAddNote.hint = null

                showActionMode()
            }
            requireActivity().invalidateOptionsMenu()

        }

        textInputEditTextTitle.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                textInputEditTextTitle.hint = null

                showActionMode()
            }
        }
    }


    private fun sendNotesToViewModel(note: Note) {
        notesViewModel.addNewNote(note)
        Log.d("Note to viewmodel:", note.toString())
    }

    private fun showActionMode() {
        val callback = object : Callback {

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
                        textViewDateUpdate.text = DateUtils.getCurrentDate()
                        val note = prepareDataForViewModel()
                        sendNotesToViewModel(note)

                        Log.d("Note from edit text", note.toString())
                        textInputEditTextAddNote.clearFocus()
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

        actionMode = materialToolbarAddNote.startActionMode(callback)
    }

    private fun prepareDataForViewModel(): Note {
        val noteContent = textInputEditTextAddNote.text?.trim().toString()
        val noteTitle = textInputEditTextTitle.text?.trim().toString()
        val date = DateUtils.getCurrentDate()

        Log.d("Note Title To ViewModel", noteTitle)

        return Note(0, noteTitle, noteContent, date)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

