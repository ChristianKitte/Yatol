package de.ckitte.myapplication.database.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

/**
 * Konverter zum Konvertieren eines Datums in einen String
 */
class DateConverter {
    /**
     * Konvertiert einen String in ein Datumsobjekt
     * @param date String? Ein String, der ein Datum repräsentiert
     * @return LocalDateTime? Eine Instanz von LocalDateTime
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
     * Konvertiert ein Datumsobjekt in einen String
     * @param date LocalDateTime? Eine Instanz von LoacalDateTime
     * @return String? Ein String, der das Datum repräsentiert
     */
    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        // month is one based!
        return date?.toString()
    }
}


