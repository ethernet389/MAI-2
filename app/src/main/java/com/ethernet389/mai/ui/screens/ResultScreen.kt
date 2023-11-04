package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ethernet389.domain.model.note.Note
import com.ethernet389.mai.R
import com.ethernet389.mai.mai.FinalWeights
import com.ethernet389.mai.matrix_extensions.MaiCoefficients
import kotlin.math.abs

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

    LazyColumn(
        modifier = modifier
    ) {
        item {
            ItemCard(modifier = Modifier.fillMaxWidth(),
                content = {
                    Text(
                        text = stringResource(R.string.rating),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = ratingBody)
                }
            )
        }
        item {
            ItemCard(
                modifier = modifier.fillMaxWidth(),
                content = {
                    Text(
                        text = stringResource(R.string.coherence_of_criteria),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    CrStatus(cr = crOfCriteriaMatrix)

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.large_padding)))

                    Text(
                        text = stringResource(R.string.coherence_of_alternatives_by_criterion),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Column {
                        val colorOfDivider = MaterialTheme.colorScheme.onTertiaryContainer
                        val thicknessOfDivider = 1.dp
                        Divider(thickness = thicknessOfDivider, color = colorOfDivider)
                        note.template.criteria.forEachIndexed { i, criterion ->
                            Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                                Text(
                                    text = criterion,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                                Divider(
                                    color = colorOfDivider,
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .width(thicknessOfDivider)
                                )
                                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                    CrStatus(cr = crsOfEachAlternativesMatrices[i])
                                }
                            }
                            Divider(thickness = thicknessOfDivider, color = colorOfDivider)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun CrStatus(
    cr: Double,
    modifier: Modifier = Modifier
) {
    val (text, isCoherent) = isConsistent(cr)
    val (color, icon) = when(isCoherent) {
        true -> colorResource(R.color.is_not_inverse_color) to Icons.Filled.Check
        false -> colorResource(R.color.is_inverse_color) to Icons.Filled.Close
    }
    Row(modifier = modifier) {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Center
        )
        Icon(
            imageVector = icon,
            tint = color,
            contentDescription = null
        )
    }
}

@Composable
@ReadOnlyComposable
private fun isConsistent(d: Double): Pair<String, Boolean> {
    return when (d <= MaiCoefficients.MAX_OF_CR) {
        true -> stringResource(R.string.coherent) to true
        false -> stringResource(R.string.non_coherent) to false
    }
}

@Composable
private fun ItemCard(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = CardDefaults
            .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        modifier = modifier.padding(dimensionResource(R.dimen.little_padding)),
        shape = MaterialTheme.shapes.medium
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            content = { content() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.medium_padding))
        )
    }
}