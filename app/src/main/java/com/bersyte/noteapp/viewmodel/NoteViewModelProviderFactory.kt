package com.bersyte.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bersyte.noteapp.repositorio.RepositorioNotas

class NoteViewModelProviderFactory(
    val app: Application,
    private val repositorioNotas: RepositorioNotas
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NoteViewModel(app, repositorioNotas) as T
    }
}