package dev.borisochieng.notewave.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.borisochieng.notewave.models.NotesContent
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlin.concurrent.Volatile

@Database(entities = [NotesContent::class], version = 1, exportSchema = false)
abstract class NotesDataBase : RoomDatabase() {

    abstract fun notesDAO(): NotesDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDataBase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): NotesDataBase {
            //if instance is not null return the DB if not create it
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesDataBase::class.java,
                    "notes_database"
                ).build()
                INSTANCE = instance

                //return instance
                instance
            }

        }
    }

}