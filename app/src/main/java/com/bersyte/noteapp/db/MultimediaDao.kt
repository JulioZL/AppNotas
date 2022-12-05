package com.bersyte.noteapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bersyte.noteapp.model.Multimedia

@Dao
interface MultimediaDao {
    @Insert
    fun insert(multimedia: Multimedia): Long

    @Query("SELECT * FROM multimedia WHERE idNote=:idNote")
    fun getMultimedia(idNote: Int): List<Multimedia>

    @Delete
    fun delete(multimedia: Multimedia)
}