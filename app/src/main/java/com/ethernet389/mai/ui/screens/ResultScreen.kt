package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.ethernet389.domain.model.note.Note
import com.ethernet389.mai.R
import com.ethernet389.mai.mai.FinalWeights

@Composable
fun ResultScreen(
    note: Note,
    finalWeights: FinalWeights,
    crsOfEachAlternativesMatrices: List<Double>,
    crOfCriteriaMatrix: Double,
    modifier: Modifier = Modifier
) {
    val ratingBody = finalWeights
        .finalRelativeWeights
        .mapIndexed { i, coefficient -> note.alternatives[i] to coefficient }
        .sortedByDescending { it.second }
        .mapIndexed { i, pair -> "${i + 1}. ${pair.first} (${pair.second.toFloat()})" }
        .reduce { acc, s -> "$acc\n$s" }

    val consistencyBody = (listOf(crOfCriteriaMatrix) + crsOfEachAlternativesMatrices)
        .map { cr -> cr.toString() }
        .reduceIndexed { i, acc, s -> "$acc\n${i + 1}. $s" }

    LazyColumn(
        modifier = modifier
    ) {
        item {
            ItemCard(title = "Rating", body = ratingBody, modifier = Modifier.fillMaxWidth())
        }
        item {
            ItemCard(title = "Consistency", body = consistencyBody, modifier = modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ItemCard(title: String, body: String, modifier: Modifier = Modifier) {
    Card(
        elevation = CardDefaults
            .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        modifier = modifier.padding(dimensionResource(R.dimen.little_padding)),
        shape = MaterialTheme.shapes.medium
    )
        {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.medium_padding))
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(text = body)
        }
    }
}