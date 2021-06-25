package de.ckitte.myapplication.database.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

/**
 *
 */
class DateConverter {
    /**
     *
     * @param date String?
     * @return LocalDateTime?
     */
    @TypeConverter
    fun timestampToDate(date: String?): LocalDateTime? {
        // month is one based!
        try {
            return LocalDateTime.parse(date)
        } catch (e: Exception) {
            return LocalDateTime.now()
        }

    }

    /**
     *
     * @param date LocalDateTime?
     * @return String?
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        // month is one based!
        return date?.toString()
    }
}


