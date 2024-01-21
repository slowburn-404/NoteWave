package dev.borisochieng.notewave.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.database.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentEditNoteBinding
import dev.borisochieng.notewave.models.NotesContent
import dev.borisochieng.notewave.viewmodels.NotesViewModel
import dev.borisochieng.notewave.viewmodels.NotesViewModelFactory
import java.util.Calendar

class EditNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var textInputLayoutEditNote: TextInputLayout
    private lateinit var textInputEditTextEditNote: TextInputEditText
    private lateinit var collapsingToolbarLayoutEditNote: CollapsingToolbarLayout
    private lateinit var materialToolbarEditNote: MaterialToolbar

    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        initViews()

        mutateViewsBasedOnETFocus()

        materialToolbarEditNote.setNavigationOnClickListener {
            navController.popBackStack()
        }
        materialToolbarEditNote.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save -> {
                    // Handle save icon press
                    val noteContent = textInputEditTextEditNote.text?.trim().toString()
                    val noteTitle = ContextCompat.getString(requireContext(), R.string.demo)
                    val date = getCurrentDate()

                    val note = NotesContent(0, noteTitle, noteContent, date)

                    sendNotesToViewModel(note)
                    Log.d("Note from edit text", note.toString())
                    Snackbar.make(binding.root, "Note saved.", Snackbar.LENGTH_SHORT).show()

                    true
                }

                else -> false
            }
        }

        return binding.root
    }

    private fun initViews() {
        navController = findNavController()
        textInputLayoutEditNote = binding.tILEditNote
        textInputEditTextEditNote = binding.tIETEditNote
        collapsingToolbarLayoutEditNote = binding.collapsingTooolbarEditNote
        materialToolbarEditNote = binding.toolBarEditNote
    }

    private fun mutateViewsBasedOnETFocus() {
        textInputEditTextEditNote.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {
                textInputLayoutEditNote.hint = null
            } else {
                textInputLayoutEditNote.hint =
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
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$day-$month-$year"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

