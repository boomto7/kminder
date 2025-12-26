package com.kminder.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kminder.domain.model.EmotionKeyword

class EmotionKeywordListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<EmotionKeyword> {
        if (value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<EmotionKeyword>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<EmotionKeyword>?): String {
        return gson.toJson(list ?: emptyList<EmotionKeyword>())
    }
}
