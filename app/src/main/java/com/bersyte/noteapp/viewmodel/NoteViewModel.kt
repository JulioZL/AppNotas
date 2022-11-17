package com.bersyte.noteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.repositorio.RepositorioNotas
import kotlinx.coroutines.launch

class NoteViewModel(
    app: Application,
    private val repositorioNotas: RepositorioNotas
) : AndroidViewModel(app) {


    fun agregarNota(note: Note) =
        viewModelScope.launch {
            repositorioNotas.insertNote(note)
        }

    fun borrarNota(note: Note) =
        viewModelScope.launch {
            repositorioNotas.deleteNote(note)
        }

    fun actualizarNota(note: Note) =
        viewModelScope.launch {
            repositorioNotas.updateNote(note)
        }

    fun getAllNote() = repositorioNotas.getAllNotes()

    fun buscarNota(query: String?) =
        repositorioNotas.searchNote(query)



}