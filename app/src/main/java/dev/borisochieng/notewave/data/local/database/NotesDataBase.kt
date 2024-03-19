package dev.borisochieng.notewave.data.local.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.borisochieng.notewave.data.models.Note
import dev.borisochieng.notewave.utils.DateConverterUtil
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlin.concurrent.Volatile

@Database(
    entities = [Note::class],
    version = NotesDataBase.LATEST_VERSION,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4)
    ]
)
@TypeConverters(DateConverterUtil::class)
abstract class NotesDataBase : RoomDatabase() {

    abstract fun notesDAO(): NotesDao

    companion object {
        const val LATEST_VERSION = 4
        @Volatile
        private var INSTANCE: NotesDataBase? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): NotesDataBase {
            //if instance is not null return the DB if not create it
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, NotesDataBase::class.java, "notes_database"
                ).build()
                INSTANCE = instance

                //return instance
                instance
            }

        }

    }

}