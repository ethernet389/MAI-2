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
import com.ethernet389.mai.mai.FinalWeights
import com.ethernet389.mai.mai.InputParameters
import com.ethernet389.mai.mai.MAI
import com.ethernet389.mai.matrix_extensions.KMatrix
import com.ethernet389.mai.matrix_extensions.MaiCoefficients
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

class MaiNoteState(note: Note) {
    constructor() : this(
        Note(
            id = -1,
            name = "",
            template = Template(id = -1, name = "", criteria = emptyList()),
            candidates = emptyList(),
            report = InputParameters(
                KMatrix(Matrix(1, 1)),
                listOf(KMatrix(Matrix(1, 1)))
            ).encodeToString()
        )
    )

    private val inputParameters = InputParameters.decodeFromString(note.report)

    val finalWeights: FinalWeights = MAI(inputParameters)

    val crOfCriteriaMatrix: Double = MaiCoefficients.RI(inputParameters.criteriaMatrix)
    val crsOfEachAlternativesMatrices: List<Double> = inputParameters
        .candidatesMatrices
        .map { matrix -> MaiCoefficients.CR(matrix) }
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

    private fun updateTemplates() {
        viewModelScope.launch {
            _uiStateFlow.update {
                it.copy(templates = templateController.loader.getTemplates())
            }
        }
    }

    private fun updateNotes() {
        viewModelScope.launch {
            _uiStateFlow.update {
                it.copy(notes = noteController.loader.getNotes())
            }
        }
    }

    private fun updateData() {
        viewModelScope.launch {
            launch { updateTemplates() }
            launch { updateNotes() }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteController.deleter.deleteNote(note)
            updateNotes()
        }
    }

    fun deleteTemplate(template: Template) {
        viewModelScope.launch {
            templateController.deleter.deleteTemplate(template)
            updateData()
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

    private var _maiNoteStateFlow = MutableStateFlow(MaiNoteState())
    val maiNoteStateFlow = _maiNoteStateFlow.asStateFlow()

    fun setNewCurrentMaiNoteState(note: Note) {
        viewModelScope.launch {
            _maiNoteStateFlow.update { MaiNoteState(note) }
        }
    }
}

fun CreationNoteState.toInputParameters(): InputParameters =
    InputParameters(
        criteriaMatrix = KMatrix(relationMatrices.first()),
        candidatesMatrices = relationMatrices
            .subList(1, relationMatrices.size)
            .map { KMatrix(it) }
    )