package com.bersyte.noteapp.repositorio

import com.bersyte.noteapp.db.NoteDatabase
import com.bersyte.noteapp.model.Tarea

class RepositorioTareas(private val db: NoteDatabase) {

    suspend fun insertTarea(tarea: Tarea) = db.getNoteDao().insertarTarea(tarea)
    suspend fun deleteTarea(tarea: Tarea) = db.getNoteDao().borrarTarea(tarea)
    suspend fun updateTarea(tarea: Tarea) = db.getNoteDao().actualizarTarea(tarea)
    fun getAllTareas() = db.getNoteDao().getAllTareas()
    fun searchTarea(query: String?) = db.getNoteDao().searchTarea(query)

}

