package dev.borisochieng.notewave.utils

import androidx.room.TypeConverter
import android.icu.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateConverterUtil {

    //converts a string to a date object
    @TypeConverter
    fun toDateFromString(dateString: String?): Date? =
        dateString?.let { SimpleDateFormat("dd MMMM yyyy h:mm a", Locale.getDefault()).parse(it) }

    @TypeConverter
    fun fromDateToString(date: Date?): String? =
        date?.let { SimpleDateFormat("dd MMMM yyyy h:mm a", Locale.getDefault()).format(it) }
}