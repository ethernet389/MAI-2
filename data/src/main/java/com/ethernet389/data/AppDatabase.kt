package com.ethernet389.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ethernet389.data.dao.NoteDao
import com.ethernet389.data.dao.TemplateDao
import com.ethernet389.data.model.DataNote
import com.ethernet389.data.model.DataTemplate

@Database(
    version = 1,
    entities = [DataTemplate::class, DataNote::class],
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getNoteDao(): NoteDao
    abstract fun getTemplateDao(): TemplateDao
}

const val APP_DATABASE_NAME = "mai_db"