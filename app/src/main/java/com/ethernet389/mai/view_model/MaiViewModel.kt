package com.ethernet389.mai.view_model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.model.template.BaseTemplate
import com.ethernet389.domain.model.template.Template
import com.ethernet389.domain.use_case.note.NotesCreator
import com.ethernet389.domain.use_case.note.NotesDeleter
import com.ethernet389.domain.use_case.note.NotesLoader
import com.ethernet389.domain.use_case.template.TemplatesCreator
import com.ethernet389.domain.use_case.template.TemplatesDeleter
import com.ethernet389.domain.use_case.template.TemplatesLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.properties.Delegates

data class Controller<Creator, Loader, Deleter>(
    val creator: Creator,
    val loader: Loader,
    val deleter: Deleter
)

data class MaiUiState(
    val templates: List<Template>,
    val notes: List<Note>
)

class MaiViewModel(
    private val noteController: Controller<NotesCreator, NotesLoader, NotesDeleter>,
    private val templateController: Controller<TemplatesCreator, TemplatesLoader, TemplatesDeleter>
) : ViewModel() {
    private val _maiUiStateFlow: MutableStateFlow<MaiUiState> = MutableStateFlow(
        MaiUiState(emptyList(), emptyList())
    )
    val maiUiStateFlow = _maiUiStateFlow.asStateFlow()

    init {
        updateData()
    }

    fun addTemplate(template: BaseTemplate): Boolean {
        var added = false
        viewModelScope.launch(Dispatchers.IO) {
            added = templateController.creator.createTemplate(template)
        }
        updateTemplates()
        return added
    }

    fun deleteTemplate(template: Template): Boolean {
        var deleted = false
        viewModelScope.launch(Dispatchers.IO) {
            deleted = templateController.deleter.deleteTemplate(template)
        }
        updateTemplates()
        return deleted
    }

    private fun updateTemplates() = viewModelScope.launch {
        lateinit var templates: List<Template>
        withContext(Dispatchers.IO) {
            templates = templateController.loader.getTemplates()
        }
        _maiUiStateFlow.update { maiUiState ->
            maiUiState.copy(templates = templates)
        }
    }

    private fun updateNotes() = viewModelScope.launch {
        lateinit var notes: List<Note>
        withContext(Dispatchers.IO) {
            notes = noteController.loader.loadNotes()
        }
        _maiUiStateFlow.update { maiUiState ->
            maiUiState.copy(notes = notes)
        }
    }

    private fun updateData() {
        updateNotes()
        updateTemplates()
    }
}