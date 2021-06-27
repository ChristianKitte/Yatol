package de.ckitte.myapplication.util

import java.time.LocalDateTime

/**
 * Enthält statische Hilfsmethoden für Datumsfunktionen
 */
class DateTimeUtil {
    companion object {
        /**
         * Erzeugt aus einem LocalDateTime einen formatierten String mit Datum und Uhrzeit für die
         * direkte Anzeige
         * @param dateTime LocalDateTime Eine INstanz von [LocalDateTime]
         * @return String Der Anzeigestring des Datums und der Uhrzeit
         */
        fun getTimeString(dateTime: LocalDateTime): String {
            val currentDayString = dateTime.dayOfMonth.toString().padStart(2, '0')
            val currentMonthString = dateTime.monthValue.toString().padStart(2, '0')
            val currentYearString = dateTime.year.toString().padStart(4, '0')
            val currentHourString = dateTime.hour.toString().padStart(2, '0')
            val currentMinuteString = dateTime.minute.toString().padStart(2, '0')

            return "Am $currentDayString.$currentMonthString.$currentYearString um $currentHourString:$currentMinuteString Uhr"
        }
    }
}