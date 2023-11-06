package com.ethernet389.mai.view_model.states

import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.model.template.Template

data class MaiUiState(
    val templates: List<Template>,
    val notes: List<Note>
) {
    constructor() : this(emptyList(), emptyList())
}