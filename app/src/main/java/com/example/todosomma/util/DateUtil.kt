package com.example.todosomma.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateUtil {
    companion object {
        private const val APP_DATE_FORMAT = "E, MMM d, yyyy, HH:ss"
        fun formatDateTimeMillis(dateTimeMillis: Long): String {
            val sdf = SimpleDateFormat(APP_DATE_FORMAT, Locale.getDefault())
            return sdf.format(Date(dateTimeMillis))
        }
    }
}