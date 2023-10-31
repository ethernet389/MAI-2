package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ethernet389.mai.R

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onDeleteNotesClick: () -> Unit,
    onDeleteUnusedTemplatesClick: () -> Unit,
    onClearAllDatabaseClick: () -> Unit
) {
    OperationsCard(
        onDeleteNotesClick = onDeleteNotesClick,
        onDeleteUnusedTemplatesClick = onDeleteUnusedTemplatesClick,
        onClearAllDatabaseClick = onClearAllDatabaseClick,
        modifier = modifier
    )
}


@Composable
private fun OperationsCard(
    modifier: Modifier = Modifier,
    onDeleteNotesClick: () -> Unit,
    onDeleteUnusedTemplatesClick: () -> Unit,
    onClearAllDatabaseClick: () -> Unit,
) {
    Card(
        modifier = modifier.padding(dimensionResource(R.dimen.little_padding))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.database),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier.padding(dimensionResource(R.dimen.medium_padding))
            )
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
            ) {
                val buttonModifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(dimensionResource(R.dimen.small_padding))
                RowItem(
                    text = stringResource(R.string.clear_all_data),
                    onClick = onClearAllDatabaseClick,
                    modifier = buttonModifier
                )
                RowItem(
                    text = stringResource(R.string.delete_unused_templates),
                    onClick = onDeleteUnusedTemplatesClick,
                    modifier = buttonModifier
                )
                RowItem(
                    text = stringResource(R.string.delete_notes),
                    onClick = onDeleteNotesClick,
                    modifier = buttonModifier
                )
            }
        }
    }
}

@Composable
private fun RowItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
        )
    }
}