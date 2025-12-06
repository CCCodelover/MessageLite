package com.example.messagelite.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeFormatter {

    fun format(timestamp: Long): String {
        val date = Date(timestamp)
        val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}
