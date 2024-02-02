package dev.borisochieng.notewave.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.database.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentEditNoteBinding
import dev.borisochieng.notewave.models.NotesContent
import dev.borisochieng.notewave.viewmodels.NotesViewModel
import dev.borisochieng.notewave.viewmodels.NotesViewModelFactory


class EditNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private val navController = findNavController()

    private val navArgs: EditNoteFragmentArgs by navArgs()
    
    private lateinit var textInputLayoutTitle: TextInputLayout
    private lateinit var textInputEditTextTitle: TextInputEditText
    private lateinit var textViewUpdatedAt: MaterialTextView
    private lateinit var textInputLayoutEditNote: TextInputLayout
    private lateinit var textInputEditTextEditNote: TextInputEditText
    
    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }
    

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditNoteBinding.inflate(layoutInflater, container, false)
        initViews()


        binding.toolBarEditNote.setNavigationOnClickListener {

            navController.popBackStack()
        }

        return binding.root
    }

    private fun initViews() {
        textInputLayoutTitle = binding.tILEditNoteTitle
        textInputEditTextTitle = binding.tIETEditNoteTitle
        textViewUpdatedAt = binding.dateUpdated
        textInputLayoutEditNote = binding.tILEditNote
        textInputEditTextEditNote = binding.tIETEditNote
    }

    private fun updateViewModel(note: NotesContent) {
        notesViewModel.editNote(note)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}