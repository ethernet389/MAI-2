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

//Note Creation Info Data Classes
val relationScale = 1..9

data class RelationInfo(
    val firstParameter: String,
    val secondParameter: String,
    val relationValue: Int = 1,
    val isInverse: Boolean = false
)

data class RelationInfoMassive(
    val relationName: String,
    val relations: List<RelationInfo>
)

data class CreationNoteInfo(
    val noteName: String,
    val relationList: List<RelationInfoMassive>
) {
    constructor() : this("", emptyList())
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

    private var _creationNoteState = MutableStateFlow(CreationNoteInfo())
    val creationNoteState = _creationNoteState.asStateFlow()

    fun updateCreationNoteState(noteName: String, template: Template, alternatives: List<String>) {
        val relationsMassive = getAllRelations(template, alternatives)
        _creationNoteState.update { CreationNoteInfo(noteName, relationsMassive) }
    }

    fun updateCreationNoteState(i: Int, j: Int, newRelationInfo: RelationInfo) {
        _creationNoteState.update {
            val newRelationList = it.relationList[i]
                .relations
                .toMutableList()
                .apply { this[j] = newRelationInfo }
            val newListOfRelationInfoMassive = it.relationList.toMutableList().apply {
                this[i] = this[i].copy(relations = newRelationList)
            }
            it.copy(relationList = newListOfRelationInfoMassive)
        }
    }

    fun dropCreationNoteState() = _creationNoteState.update { CreationNoteInfo() }
}

fun getAllRelationsCombinations(relationMembers: List<String>): List<RelationInfo> {
    val relations = mutableListOf<RelationInfo>()
    for (i in relationMembers.indices) {
        val first = relationMembers[i]
        for (j in (i + 1)..relationMembers.lastIndex) {
            val second = relationMembers[j]
            relations.add(RelationInfo(first, second))
        }
    }
    return relations
}

fun getAllRelations(
    template: Template,
    alternatives: List<String>
): List<RelationInfoMassive> {
    val relations = mutableListOf<RelationInfoMassive>()
    //Template base relations
    val templateRelations = getAllRelationsCombinations(relationMembers = template.criteria)
    if (templateRelations.isNotEmpty()) {
        relations.add(
            RelationInfoMassive(
                relationName = template.name,
                relations = templateRelations
            )
        )
    }
    //Alternatives base relations
    val alternativesRelations = getAllRelationsCombinations(relationMembers = alternatives)
    if (alternativesRelations.isNotEmpty()) {
        for (criterion in template.criteria) {
            val relationMassive = RelationInfoMassive(
                relationName = criterion, relations = alternativesRelations
            )
            relations.add(relationMassive)
        }
    }
    return relations
}