package de.ckitte.myapplication.database.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

class DateConverter {
    @TypeConverter
    fun timestampToDate(date: String?): LocalDateTime? {
        try {
            return LocalDateTime.parse(date)
        } catch (e: Exception) {
            return LocalDateTime.now()
        }

    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }
}


