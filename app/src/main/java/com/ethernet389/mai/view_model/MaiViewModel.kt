package com.ethernet389.mai.view_model

import androidx.lifecycle.ViewModel
import com.ethernet389.domain.use_case.note.NotesCreator
import com.ethernet389.domain.use_case.note.NotesDeleter
import com.ethernet389.domain.use_case.note.NotesLoader
import com.ethernet389.domain.use_case.template.TemplatesCreator
import com.ethernet389.domain.use_case.template.TemplatesDeleter
import com.ethernet389.domain.use_case.template.TemplatesLoader

data class Controller<Creator, Loader, Deleter> (
    val creator: Creator,
    val loader: Loader,
    val deleter: Deleter
)

class MaiViewModel(
    private val notesController: Controller<NotesCreator, NotesLoader, NotesDeleter>,
    private val templateController: Controller<TemplatesCreator, TemplatesLoader, TemplatesDeleter>
) : ViewModel() {

}