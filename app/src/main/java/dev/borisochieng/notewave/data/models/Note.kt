package dev.borisochieng.notewave.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.borisochieng.notewave.data.utils.DateConverterUtil
import dev.borisochieng.notewave.data.utils.DateUtil

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "time_stamp")
    @TypeConverters(DateConverterUtil::class) val timeStamp: String = DateUtil.getCurrentDate()
)
