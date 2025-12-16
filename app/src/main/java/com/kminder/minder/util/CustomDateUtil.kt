package com.kminder.minder.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object CustomDateUtil {
    fun getCurrentDate(currentLocale: Locale) : String {
        try {
            // 한국어(ko)인 경우와 그 외(영어권 등)를 구분하여 포맷 설정
            val pattern = if (currentLocale.language == "ko") {
                "MM월 dd일 EEEE"
            } else {
                "EEEE, MMMM d"
            }
            return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern, currentLocale))
        } catch (e: Exception) {
            return if (currentLocale.language == "ko") "12월 16일 월요일" else "Monday, December 16"
        }
    }
}