package com.ethernet389.mai.view_model.states

import Jama.Matrix
import com.ethernet389.domain.model.template.Template

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

