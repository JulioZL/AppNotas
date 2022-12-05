package com.bersyte.noteapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.bersyte.noteapp.databinding.ActivityMainBinding
import com.bersyte.noteapp.db.NoteDatabase
import com.bersyte.noteapp.repositorio.RepositorioNotas
import com.bersyte.noteapp.repositorio.RepositorioTareas
import com.bersyte.noteapp.viewmodel.NoteViewModel
import com.bersyte.noteapp.viewmodel.NoteViewModelProviderFactory
import com.bersyte.noteapp.viewmodel.TareaViewModel
import com.bersyte.noteapp.viewmodel.TareaViewModelProviderFactory

class MainActivity : AppCompatActivity() {


    lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    lateinit var noteViewModel: NoteViewModel
    lateinit var tareaViewModel: TareaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpViewModel()
        setUpViewModelTarea()

    }

    /*override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.updateNoteFragment) {
            this.toast("No Updates")
        }
        super.onBackPressed()
    }*/

    private fun setUpViewModel() {
        val noteDataBase = NoteDatabase.getInstance(this)
        val noteRepository = RepositorioNotas(
            noteDataBase
        )

        val viewModelProviderFactory = NoteViewModelProviderFactory(
            application,
            noteRepository
        )

        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )
            .get(NoteViewModel::class.java)
    }

    private fun setUpViewModelTarea() {
        val tareaDataBase = NoteDatabase.getInstance(this)
        val tareaRepository = RepositorioTareas(
            tareaDataBase
        )

        val viewModelProviderFactory = TareaViewModelProviderFactory(
            application,
            tareaRepository
        )

        tareaViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )
            .get(TareaViewModel::class.java)
    }
}