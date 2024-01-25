package dev.borisochieng.notewave.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.borisochieng.notewave.database.NoteApplication
import dev.borisochieng.notewave.databinding.FragmentNoteTitleBottomsheetBinding
import dev.borisochieng.notewave.viewmodels.NotesViewModel
import dev.borisochieng.notewave.viewmodels.NotesViewModelFactory

class NoteTitleModalBottomSheet : BottomSheetDialogFragment() {


    private lateinit var binding: FragmentNoteTitleBottomsheetBinding

    private val notesViewModel: NotesViewModel by activityViewModels {
        NotesViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteTitleBottomsheetBinding.inflate(layoutInflater)
        

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bTAddTitle.setOnClickListener {
            val title = binding.tIETAddTitle.text?.trim().toString()
            Log.d("Title", title)
            notesViewModel.setTitle(title)
            dismiss()
        }
    }

    companion object {
        const val TAG = "NoteTitleModalBottomSheet"

    }

}