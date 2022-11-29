package com.bersyte.noteapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bersyte.noteapp.databinding.ActivityMainBinding
import com.bersyte.noteapp.db.NoteDatabase
import com.bersyte.noteapp.db.TareaDatabase
import com.bersyte.noteapp.repositorio.RepositorioNotas
import com.bersyte.noteapp.repositorio.RepositorioTareas
import com.bersyte.noteapp.viewmodel.NoteViewModel
import com.bersyte.noteapp.viewmodel.NoteViewModelProviderFactory
import com.bersyte.noteapp.viewmodel.TareaViewModel
import com.bersyte.noteapp.viewmodel.TareaViewModelProviderFactory

class MainActivity : AppCompatActivity() {

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

    private fun setUpViewModel() {
        val repositorioNotas = RepositorioNotas(
            NoteDatabase(this)
        )

        val viewModelProviderFactory =
            NoteViewModelProviderFactory(
                application, repositorioNotas
            )

        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        ).get(NoteViewModel::class.java)
    }

    private fun setUpViewModelTarea() {
        val repositorioTareas = RepositorioTareas(
            TareaDatabase(this)
        )

        val viewModelProviderFactory =
            TareaViewModelProviderFactory(
                application, repositorioTareas
            )

        tareaViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        ).get(TareaViewModel::class.java)
    }

}