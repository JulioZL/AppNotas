package com.bersyte.noteapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bersyte.noteapp.model.Note
import com.bersyte.noteapp.model.Tarea

@Database(entities = [Tarea::class], version = 1)
abstract class TareaDatabase: RoomDatabase() {

    abstract fun getTareaDao(): TareaDao

    companion object {
        @Volatile
        private var instance: TareaDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?:
        synchronized(LOCK) {
            instance ?:
            createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TareaDatabase::class.java,
                "tarea_db"
            ).build()
    }


}