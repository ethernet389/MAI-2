package com.ethernet389.mai.di

import com.ethernet389.domain.use_case.note.NotesCreator
import com.ethernet389.domain.use_case.note.NotesDeleter
import com.ethernet389.domain.use_case.note.NotesLoader
import com.ethernet389.domain.use_case.template.TemplatesCreator
import com.ethernet389.domain.use_case.template.TemplatesDeleter
import com.ethernet389.domain.use_case.template.TemplatesLoader
import com.ethernet389.mai.view_model.Controller
import com.ethernet389.mai.view_model.MaiViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    factory<Controller<NotesCreator, NotesLoader, NotesDeleter>> {
        Controller(get(), get(), get())
    }
    factory<Controller<TemplatesCreator, TemplatesLoader, TemplatesDeleter>> {
        Controller(get(), get(), get())
    }

    viewModel {
        MaiViewModel(notesController = get(), templateController = get())
    }
}