package com.ethernet389.mai.view_model.states

import Jama.Matrix
import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.model.template.Template
import com.ethernet389.mai.mai.FinalWeights
import com.ethernet389.mai.mai.InputParameters
import com.ethernet389.mai.mai.MAI
import com.ethernet389.mai.matrix_extensions.KMatrix
import com.ethernet389.mai.matrix_extensions.MaiCoefficients

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