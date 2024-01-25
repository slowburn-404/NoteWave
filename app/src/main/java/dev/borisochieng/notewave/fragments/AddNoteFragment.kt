package dev.borisochieng.notewave.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.database.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentAddNoteBinding
import dev.borisochieng.notewave.models.NotesContent
import dev.borisochieng.notewave.viewmodels.NotesViewModel
import dev.borisochieng.notewave.viewmodels.NotesViewModelFactory
import java.util.Calendar

class AddNoteFragment : Fragment() {

    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var textInputLayoutAddNote: TextInputLayout
    private lateinit var textInputEditTextAddNote: TextInputEditText
    private lateinit var collapsingToolbarLayoutAddNote: CollapsingToolbarLayout
    private lateinit var materialToolbarAddNote: MaterialToolbar

    private var actionMode: ActionMode? = null

    private val notesViewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }

    private lateinit var noteTitle: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        initViews()
        noteTitle =
            ContextCompat.getString(requireContext(), R.string.untitled_note)

        Log.d("NoteTitle if null", noteTitle)

        materialToolbarAddNote.title = noteTitle

        showBottomSheet()
        getTitleFromViewModel()
        mutateViewsBasedOnETFocus()

        materialToolbarAddNote.setNavigationOnClickListener {
            navController.popBackStack()
        }




        return binding.root
    }

    private fun initViews() {
        navController = findNavController()
        textInputLayoutAddNote = binding.tILAddNote
        textInputEditTextAddNote = binding.tIETAddNote
        collapsingToolbarLayoutAddNote = binding.collapsingToolbarAddNote
        materialToolbarAddNote = binding.toolBarAddNote
    }

    private fun mutateViewsBasedOnETFocus() {
        textInputEditTextAddNote.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutAddNote.hint = null

                showActionModeOnTextChange()
            } else {
                textInputLayoutAddNote.hint =
                    ContextCompat.getString(requireContext(), R.string.edit_note)
            }

            requireActivity().invalidateOptionsMenu()

        }
    }


    private fun sendNotesToViewModel(note: NotesContent) {
        notesViewModel.addNewNote(note)
        Log.d("Note to viewmodel:", note.toString())
    }

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_WEEK)

        return "$day-$month-$year"
    }

    private fun showActionMode() {
        val callback = object : Callback {

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
                        sendNotesToViewModel(note)
                        Log.d("Note from edit text", note.toString())
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

    private fun showActionModeOnTextChange() {

        textInputEditTextAddNote.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                showActionMode()
            }


        })
    }

    private fun prepareDataForViewModel(): NotesContent {
        val noteContent = textInputEditTextAddNote.text?.trim().toString()
        val date = getCurrentDate()

        Log.d("Note Title To ViewModel", noteTitle)

        return NotesContent(0, noteTitle, noteContent, date)


    }

    private fun showBottomSheet() {
        val bottomSheet = NoteTitleModalBottomSheet()
        bottomSheet.show(requireActivity().supportFragmentManager, NoteTitleModalBottomSheet.TAG)
    }

    private fun getTitleFromViewModel() {
        notesViewModel.noteTitle.observe(viewLifecycleOwner, Observer { title ->
            noteTitle = title
            Log.d("Note Title From ViewModel", noteTitle)

            materialToolbarAddNote.title = title
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

