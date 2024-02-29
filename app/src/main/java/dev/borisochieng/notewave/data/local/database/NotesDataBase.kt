package dev.borisochieng.notewave.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.borisochieng.notewave.data.models.Note
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized
import kotlin.concurrent.Volatile

@Database(
    entities = [Note::class],
    version = 2,
    exportSchema = false,
)
abstract class NotesDataBase : RoomDatabase() {

    abstract fun notesDAO(): NotesDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDataBase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS notes_table ( note_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, updated_at TEXT NOT NULL);")

            }

        }
        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS notes_table ( note_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, updated_at TEXT NOT NULL);")

            }

        }

        @OptIn(InternalCoroutinesApi::class)
        fun getDatabase(context: Context): NotesDataBase {
            //if instance is not null return the DB if not create it
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, NotesDataBase::class.java, "notes_database"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
                INSTANCE = instance

                //return instance
                instance
            }

        }
    }

}