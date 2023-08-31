package com.ethernet389.mai.di

import com.ethernet389.domain.use_case.note.NotesCreator
import com.ethernet389.domain.use_case.note.NotesDeleter
import com.ethernet389.domain.use_case.note.NotesLoader
import com.ethernet389.domain.use_case.template.TemplatesCreator
import com.ethernet389.domain.use_case.template.TemplatesDeleter
import com.ethernet389.domain.use_case.template.TemplatesLoader
import org.koin.dsl.module

val domainModule = module {
    factory {
        NotesCreator(noteRepository = get())
    }
    factory {
        NotesLoader(noteRepository = get())
    }
    factory {
        NotesDeleter(noteRepository = get())
    }

    factory {
        TemplatesCreator(templateRepository = get())
    }
    factory {
        TemplatesLoader(templateRepository = get())
    }
    factory {
        TemplatesDeleter(templateRepository = get())
    }
}