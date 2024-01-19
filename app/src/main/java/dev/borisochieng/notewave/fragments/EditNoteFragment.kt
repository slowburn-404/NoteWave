package dev.borisochieng.notewave.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ActionMode
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dev.borisochieng.notewave.R
import dev.borisochieng.notewave.databinding.FragmentEditNoteBinding

class EditNoteFragment : Fragment() {

    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var textInputLayoutEditNote: TextInputLayout
    private lateinit var textInputEditTextEditNote: TextInputEditText
    private lateinit var collapsingToolbarLayoutEditNote: CollapsingToolbarLayout
    private lateinit var materialToolbarEditNote: MaterialToolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        initViews()

        mutateViewsBasedOnETFocus()

        materialToolbarEditNote.setNavigationOnClickListener {
            navController.popBackStack()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        materialToolbarEditNote.inflateMenu(R.menu.contextual_action_bar_edit_note)

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
                showContextualActionBar()
            } else {
                textInputLayoutEditNote.hint =
                    ContextCompat.getString(requireContext(), R.string.edit_note)
            }

            requireActivity().invalidateOptionsMenu()

        }
    }

    private fun showContextualActionBar() {
        val callback = object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(R.menu.contextual_action_bar_edit_note, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.save -> {
                        // Handle save icon press
                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
            }
        }
       // val actionMode = requireActivity().startActionMode(callback)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

