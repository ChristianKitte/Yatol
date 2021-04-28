package de.ckitte.myapplication.database.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DateConverter {
    @TypeConverter
    fun timestampToDate(date: String?): LocalDateTime? {
        //from UNIX Time Second since 1.1.1970 0 Uhr
        return date?.let {
            LocalDateTime.parse(it)
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        //to UNIX Time Second since 1.1.1970 0 Uhr
        return date?.let {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            it.format(formatter)
        }
    }
}


