package com.ethernet389.mai.mai

import com.ethernet389.mai.matrix_extensions.relativeWeights

fun MAI(inputParameters: InputParameters): FinalWeights {
    val criteriaMatrix = inputParameters.criteriaMatrix.value
    val candidatesMatrix = inputParameters.candidatesMatrices.map { it.value }
    val numAlt = candidatesMatrix.first().columnDimension

    val listOfWeights = mutableListOf<MutableList<Double>>()
    for (e in candidatesMatrix) listOfWeights.add(e.relativeWeights.toMutableList())
    val retListOfWeights = listOfWeights.map { it.toList() }

    val perWeights = criteriaMatrix.relativeWeights
    for (i in perWeights.indices) {
        for (j in 0 until numAlt) listOfWeights[i][j] *= perWeights[i]
    }

    val result = MutableList(numAlt) { 0.0 }
    for (i in perWeights.indices) {
        for (j in 0 until numAlt) result[j] += listOfWeights[i][j]
    }

    return FinalWeights(relativeWeights = retListOfWeights, finalRelativeWeights = result)
}