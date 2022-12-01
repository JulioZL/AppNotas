package com.bersyte.noteapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.bersyte.noteapp.model.Tarea
import kotlin.reflect.KParameter

@Database(entities = [Tarea::class], version = 1)
abstract class TareaDatabase : RoomDatabase() {

    abstract fun getTareaDao(): TareaDao

    companion object {

        @Volatile
        private var INSTANCE: TareaDatabase? = null

        fun getInstance(context: Context): TareaDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TareaDatabase::class.java,
                        "task_db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}