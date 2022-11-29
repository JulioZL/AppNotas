package com.bersyte.noteapp.repositorio

import com.bersyte.noteapp.db.TareaDatabase
import com.bersyte.noteapp.model.Tarea

class RepositorioTareas(private val db: TareaDatabase) {

    suspend fun insertTarea(tarea: Tarea) = db.getTareaDao().insertarTarea(tarea)
    suspend fun deleteTarea(tarea: Tarea) = db.getTareaDao().borrarTarea(tarea)
    suspend fun updateTarea(tarea: Tarea) = db.getTareaDao().actualizarTarea(tarea)
    fun getAllTareas() = db.getTareaDao().getAllTareas()
    fun searchTarea(query: String?) = db.getTareaDao().searchTarea(query)

}

