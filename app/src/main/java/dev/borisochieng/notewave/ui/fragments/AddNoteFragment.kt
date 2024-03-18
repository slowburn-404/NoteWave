package dev.borisochieng.notewave.ui.fragments

import android.os.Bundle
import android.view.ActionMode.Callback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ActionMode
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import dev.borisochieng.notewave.NoteApplication
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.databinding.FragmentAddNoteBinding
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.utils.DateUtil
import dev.borisochieng.notewave.ui.viewmodels.AddNoteViewModel
import dev.borisochieng.notewave.ui.viewmodels.AddNoteViewModelFactory

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
    private lateinit var appBarAddNote: AppBarLayout

    private var actionMode: ActionMode? = null

    private val addNoteViewModel: AddNoteViewModel by viewModels {
        AddNoteViewModelFactory((requireActivity().application as NoteApplication).notesRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        initViews()



        mutateViewsBasedOnETFocus()

        materialToolbarAddNote.setNavigationOnClickListener {
            navController.popBackStack()
        }
        textViewDateUpdate.text = DateUtil.getCurrentDate()

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
        appBarAddNote = binding.aBAddNote
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
            if (hasFocus) {
                textInputEditTextTitle.hint = null

                showActionMode()
            }
        }
    }


    private fun sendNotesToViewModel(note: Note) {
        addNoteViewModel.addNewNote(note)
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
                        val note = prepareDataForViewModel()
                        textViewDateUpdate.text =  note.timeStamp
                        sendNotesToViewModel(note)
                        //textInputEditTextAddNote.clearFocus()
                        navController.popBackStack()
                        Toast.makeText(requireContext(), "Note saved", Toast.LENGTH_SHORT).show()
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

        return Note(0, noteTitle, noteContent)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

