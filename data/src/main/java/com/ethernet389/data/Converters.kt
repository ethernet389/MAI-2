package com.ethernet389.data

import androidx.room.TypeConverter
import com.ethernet389.data.model.DataNote
import com.ethernet389.data.model.DataTemplate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromDataNote(dataNote: DataNote): String {
        return Json.encodeToString(dataNote)
    }

    @TypeConverter
    fun toDataNote(json: String): DataNote {
        return Json.decodeFromString(json)
    }

    @TypeConverter
    fun fromDataTemplate(dataTemplate: DataTemplate): String {
        return Json.encodeToString(dataTemplate)
    }

    @TypeConverter
    fun toDataTemplate(json: String): DataTemplate {
        return Json.decodeFromString(json)
    }
}