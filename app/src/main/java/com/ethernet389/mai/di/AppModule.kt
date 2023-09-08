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
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val NOTE_CONTROLLER = "noteController"
const val TEMPLATE_CONTROLLER = "templateController"

val appModule = module {
    factory<Controller<NotesCreator, NotesLoader, NotesDeleter>>(named(NOTE_CONTROLLER)) {
        Controller(get(), get(), get())
    }
    factory<Controller<TemplatesCreator, TemplatesLoader, TemplatesDeleter>>(
        named(TEMPLATE_CONTROLLER)
    ) {
        Controller(get(), get(), get())
    }

    viewModel {
        MaiViewModel(
            noteController = get(named(NOTE_CONTROLLER)),
            templateController = get(named(TEMPLATE_CONTROLLER))
        )
    }
}