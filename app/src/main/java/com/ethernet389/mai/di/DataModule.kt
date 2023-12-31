package com.ethernet389.mai.di

import androidx.compose.ui.text.intl.Locale
import androidx.room.Room
import com.ethernet389.data.APP_DATABASE_NAME
import com.ethernet389.data.AppDatabase
import com.ethernet389.data.repository.RoomNoteRepository
import com.ethernet389.data.repository.RoomTemplateRepository
import com.ethernet389.domain.repository.NoteRepository
import com.ethernet389.domain.repository.TemplateRepository
import org.koin.dsl.module

val dataModule = module {
    single {
        val startDbName = "${Locale.current.language}-templates.db"
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            APP_DATABASE_NAME
        ).createFromAsset(startDbName).build()
    }
    single {
        val database: AppDatabase = get()
        database.getNoteDao()
    }
    single {
        val database: AppDatabase = get()
        database.getTemplateDao()
    }
    single<NoteRepository> {
        RoomNoteRepository(
            noteDao = get(),
            templateDao = get()
        )
    }
    single<TemplateRepository> {
        RoomTemplateRepository(templateDao = get())
    }
}