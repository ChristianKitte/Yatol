package de.ckitte.myapplication.util

import java.time.LocalDateTime

class DateTimeUtil {
    companion object {
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