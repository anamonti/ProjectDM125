package com.anajulia.mytasks.utils

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object Utils {
    fun formatLocalDateToString(date: LocalDate?): String? {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date?.format(dateFormatter)
    }

    fun convertToLocalDate(dateString: String): LocalDate? {
        return try {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            LocalDate.parse(dateString, dateFormatter)
        } catch (e: DateTimeParseException) {
            println("Invalid date format. Expected format: dd/MM/yyyy")
            null
        }
    }

    fun convertToLocalTime(timeString: String): LocalTime? {
        return try {
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            LocalTime.parse(timeString, timeFormatter)
        } catch (e: DateTimeParseException) {
            println("Invalid time format. Expected format: HH:mm")
            null
        }
    }
}