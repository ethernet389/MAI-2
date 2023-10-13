package com.ethernet389.mai.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.model.template.Template
import com.ethernet389.domain.use_case.note.NotesCreator
import com.ethernet389.domain.use_case.note.NotesDeleter
import com.ethernet389.domain.use_case.note.NotesLoader
import com.ethernet389.domain.use_case.template.TemplatesCreator
import com.ethernet389.domain.use_case.template.TemplatesDeleter
import com.ethernet389.domain.use_case.template.TemplatesLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Controller<Creator, Loader, Deleter>(
    val creator: Creator,
    val loader: Loader,
    val deleter: Deleter
)

data class MaiUiState(
    val templates: List<Template>,
    val notes: List<Note>
) {
    constructor() : this(emptyList(), emptyList())
}

class MaiViewModel(
    private val noteController: Controller<NotesCreator, NotesLoader, NotesDeleter>,
    private val templateController: Controller<TemplatesCreator, TemplatesLoader, TemplatesDeleter>
) : ViewModel() {

    private var _uiStateFlow = MutableStateFlow(MaiUiState())
    val uiStateFlow = _uiStateFlow.asStateFlow()
    init {
        updateData()
    }

    fun createTemplate(template: Template) {
        viewModelScope.launch {
            templateController.creator.createTemplate(template)
            updateTemplates()
        }
    }

    fun updateTemplates() {
        viewModelScope.launch {
            _uiStateFlow.update {
                it.copy(templates = templateController.loader.getTemplates())
            }
        }
    }

    fun updateNotes() {
        viewModelScope.launch {
            _uiStateFlow.update {
                it.copy(notes = noteController.loader.getNotes())
            }
        }
    }

    fun updateData() {
        viewModelScope.launch {
            launch { updateTemplates() }
            launch { updateNotes() }
        }
    }
}