package com.bersyte.noteapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val noteTitle: String,
    val noteSubTitle: String,
    val notetvDate: String,
    val noteBody: String
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
