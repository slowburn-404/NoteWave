package dev.borisochieng.notewave.data.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import java.util.Locale

object DateUtils {
    private var formatter = SimpleDateFormat("dd MMMM yyyy h:mm a", Locale.getDefault())

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return formatter.format(calendar.time)
    }
}