package dev.borisochieng.notewave.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.borisochieng.notewave.NoteApplication
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.databinding.ActivityMainBinding
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModel
import dev.borisochieng.notewave.ui.viewmodels.NotesViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var allNotesFromViewModel: MutableList<Note>? = mutableListOf()

    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory((this.application as NoteApplication).notesRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)


        allNotesFromViewModel = notesViewModel.allNotes.value


        //Initialize ViewModel in the splash screen
        val content: View = rootView
        content.viewTreeObserver.addOnPreDrawListener(
            object: ViewTreeObserver.OnPreDrawListener{
                override fun onPreDraw(): Boolean {
                    return if (allNotesFromViewModel.isNullOrEmpty()) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        false
                    }
                }
            }
        )

    }
}