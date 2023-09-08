package com.ethernet389.data

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun stringListToJson(lst: List<String>): String {
        return Json.encodeToString(lst)
    }

    @TypeConverter
    fun jsonToListString(json: String): List<String> {
        return Json.decodeFromString(json)
    }
}