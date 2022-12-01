package com.bersyte.noteapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String,
    val noteSubTitle: String,
    val notetvDate: String,
    val noteBody: String,
    val noteImagen: String,
    val noteVideo: String,
    val noteAudio: String,
) : Parcelable

@Entity(tableName = "tareas")
@Parcelize
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val tareaTitle: String,
    val tareaSubTitle: String,
    val tareatvDate: String,
    val tareaBody: String
) : Parcelable
