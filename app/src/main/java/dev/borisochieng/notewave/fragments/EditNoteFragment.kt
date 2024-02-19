package dev.borisochieng.notewave.fragments

import android.icu.util.Calendar
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
import androidx.navigation.fragment.navArgs
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.database.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentEditNoteBinding
import dev.borisochieng.notewave.models.Note
import dev.borisochieng.notewave.viewmodels.NotesViewModel
import dev.borisochieng.notewave.viewmodels.NotesViewModelFactory


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

    private var notesList = mutableListOf<Note>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditNoteBinding.inflate(layoutInflater, container, false)
        initViews()


        binding.materialToolBarEditNote.setNavigationOnClickListener {

            navController.popBackStack()
        }

        notesViewModel.getAllNotes.observe(requireActivity(), Observer { noteListFromViewModel ->
            //notesList.clear()
            noteListFromViewModel?.forEach { note ->
                notesList.add(Note(note.noteId, note.title, note.content, note.updatedAt))
            }
        })
        val noteIdFromNotesListFragment = navArgs.noteId

        Log.d("NotesList :: Note ID", "$notesList :: $noteIdFromNotesListFragment")

        val selectedNote = filterNotesList(notesList, noteIdFromNotesListFragment)

        Log.d("Selected Note", selectedNote.toString())


        textInputEditTextTitle.setText(selectedNote?.title)
        textInputLayoutTitle.hint = null
        textInputEditTextEditNote.setText(selectedNote?.content)
        textInputLayoutEditNote.hint = null

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
                inflater?.inflate(R.menu.save_note, menu)

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

    private fun filterNotesList(noteList: MutableList<Note>, id: String): Note? {

        return noteList.find {
            it.noteId.toString() == id
        }
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$day-$month-$year"
    }

    private fun prepareDataForViewModel(): Note {
        val noteId = navArgs.noteId.toLong()
        val title = textInputEditTextTitle.text?.trim().toString()
        val content = textInputEditTextEditNote.text?.trim().toString()

        return Note(noteId, title, content, getCurrentDate())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}