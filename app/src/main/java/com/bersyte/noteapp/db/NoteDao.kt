package com.bersyte.noteapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bersyte.noteapp.model.Note

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarNota(note: Note)

    @Update
    suspend fun actualizarNota(note: Note)

    @Delete
    suspend fun borrarNota(note: Note)

    @Query("SELECT * FROM NOTES ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM NOTES WHERE noteTitle LIKE :query OR noteBody LIKE:query or noteSubTitle LIKE:query")
    fun searchNote(query: String?): LiveData<List<Note>>
}