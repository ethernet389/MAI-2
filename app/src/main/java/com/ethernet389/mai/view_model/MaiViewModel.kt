package com.ethernet389.mai.view_model

import Jama.Matrix
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ethernet389.domain.model.note.BaseNote
import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.model.template.Template
import com.ethernet389.domain.use_case.note.NotesCreator
import com.ethernet389.domain.use_case.note.NotesDeleter
import com.ethernet389.domain.use_case.note.NotesLoader
import com.ethernet389.domain.use_case.template.TemplatesCreator
import com.ethernet389.domain.use_case.template.TemplatesDeleter
import com.ethernet389.domain.use_case.template.TemplatesLoader
import com.ethernet389.mai.mai.InputParameters
import com.ethernet389.mai.matrix_extensions.KMatrix
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

//Note Creation Info Data Classes
val relationScale = 1.0..9.0

//First Matrix Is Template Relation
data class CreationNoteState(
    val noteName: String,
    val template: Template,
    val alternatives: List<String>,
    val relationMatrices: List<Matrix>,
) {
    //Check the matrices for consistency
    init {
        //Check sizes
        require(relationMatrices.size == template.criteria.size + 1)
        //Template matrix
        with(relationMatrices.first()) {
            require(
                this.columnDimension == this.rowDimension &&
                        this.columnDimension == template.criteria.size
            )
        }
        //Candidates matrices
        for (i in 1..relationMatrices.lastIndex) {
            require(
                relationMatrices[i].columnDimension == relationMatrices[i].rowDimension &&
                        relationMatrices[i].columnDimension == alternatives.size
            )
        }
    }

    constructor() : this(
        noteName = "",
        template = Template(),
        alternatives = emptyList(),
        relationMatrices = listOf(Matrix(0, 0))
    )

    constructor(
        noteName: String,
        template: Template,
        alternatives: List<String>
    ) : this(
        noteName = noteName,
        template = template,
        alternatives = alternatives,
        relationMatrices = listOf(
            Matrix(
                template.criteria.size,
                template.criteria.size,
                relationScale.start
            )
        ) +
                List(template.criteria.size) {
                    Matrix(
                        alternatives.size,
                        alternatives.size,
                        relationScale.start
                    )
                }
    )
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

    fun deleteAllNotes() {
        viewModelScope.launch {
            noteController.deleter.deleteAllNotes()
            updateNotes()
        }
    }
    fun deleteUnusedTemplates() {
        viewModelScope.launch {
            templateController.deleter.deleteUnusedTemplates()
            updateTemplates()
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            deleteAllNotes()
            deleteUnusedTemplates()
        }
    }

    private var _creationNoteState = MutableStateFlow(CreationNoteState())
    val creationNoteState = _creationNoteState.asStateFlow()

    fun postNoteFromCreationNoteState() {
        viewModelScope.launch {
            val inputParameters = creationNoteState.value.toInputParameters()
            val newNote = with(creationNoteState.value) {
                BaseNote(
                    name = noteName,
                    template = template,
                    alternatives = alternatives,
                    report = inputParameters.encodeToString()
                )
            }
            noteController.creator.createNote(newNote)
            updateNotes()
        }
    }

    fun createNewCreationNoteState(
        noteName: String,
        template: Template,
        alternatives: List<String>
    ) {
        _creationNoteState.update {
            CreationNoteState(
                noteName = noteName,
                template = template,
                alternatives = alternatives
            )
        }
    }

    //h - position in relation list, i - position of row, j - position of column
    //TODO: Doesn't work with 1 criteria
    fun updateMatrixByIndex(h: Int, i: Int, j: Int, newValue: Double) {
        require(newValue in relationScale)

        val index = if (creationNoteState.value.template.criteria.size == 1) h + 1 else h
        _creationNoteState.update {
            it.copy(
                relationMatrices = it.relationMatrices
                    .apply {
                        this[index][i, j] = newValue
                        this[index][j, i] = 1 / newValue
                    }
            )
        }
    }

    fun swapValuesMatrix(h: Int, i: Int, j: Int) {
        val index = if (creationNoteState.value.template.criteria.size == 1) h + 1 else h
        _creationNoteState.update {
            it.copy(
                relationMatrices = it.relationMatrices
                    .apply {
                        val temp = this[index][i, j]
                        this[index][i, j] = this[index][j, i]
                        this[index][j, i] = temp
                    }
            )
        }
    }

    fun dropCreationNoteState() = _creationNoteState.update { CreationNoteState() }
}

fun CreationNoteState.toInputParameters(): InputParameters =
    InputParameters(
        criteriaMatrix = KMatrix(relationMatrices.first()),
        candidatesMatrices = relationMatrices
            .subList(1, relationMatrices.size)
            .map { KMatrix(it) }
    )

