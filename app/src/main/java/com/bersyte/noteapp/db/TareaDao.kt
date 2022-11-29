package com.bersyte.noteapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.model.Tarea

@Dao
interface TareaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(tarea: Tarea)

    @Update
    suspend fun actualizarTarea(tarea: Tarea)

    @Delete
    suspend fun borrarTarea(tarea: Tarea)

    @Query("SELECT * FROM tareas ORDER BY id DESC")
    fun getAllTareas(): LiveData<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE tareaTitle LIKE :query OR tareaBody LIKE:query or tareaSubTitle LIKE:query")
    fun searchTarea(query: String?): LiveData<List<Tarea>>
}