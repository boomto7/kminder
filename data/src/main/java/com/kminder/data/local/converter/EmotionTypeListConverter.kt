package com.kminder.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kminder.domain.model.EmotionType

class EmotionTypeListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<EmotionType> {
        if (value.isNullOrEmpty()) return emptyList()
        val listType = object : TypeToken<List<EmotionType>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: List<EmotionType>?): String {
        return gson.toJson(list ?: emptyList<EmotionType>())
    }
}
