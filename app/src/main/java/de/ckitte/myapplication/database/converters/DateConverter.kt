package de.ckitte.myapplication.database.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.util.*

class DateConverter {
    @TypeConverter
    fun timestampToDate(date: Long?): LocalDateTime? {
        //from UNIX Time Second since 1.1.1970 0 Uhr
        return date?.let { LocalDateTime.ofInstant(date) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        //to UNIX Time Second since 1.1.1970 0 Uhr
        return date?.let { }
    }
}


