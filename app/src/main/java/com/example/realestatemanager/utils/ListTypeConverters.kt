package com.example.realestatemanager.utils

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class ListTypeConverters {
    @TypeConverter
    fun stringToJson(json: String?): List<String> {
        val gson = Gson()
        val type =
            object : TypeToken<List<String?>?>() {}.type
        return gson.fromJson(json, type)
    }

    @TypeConverter
    fun jsonToString(list: List<String?>?): String {
        val gson = Gson()
        val type =
            object : TypeToken<List<String?>?>() {}.type
        return gson.toJson(list, type)
    }
}