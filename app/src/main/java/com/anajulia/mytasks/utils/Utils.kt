package com.anajulia.mytasks.utils

import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object Utils {
    fun formatLocalDateToStandard(date: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date.format(dateFormatter)
    }

    fun formatLocalDateToFull(date: LocalDate): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        return date.format(dateFormatter)
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

    fun saveEmail(context: Context, email: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.apply()
    }

    fun getEmail(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("user_email", null)
    }
}