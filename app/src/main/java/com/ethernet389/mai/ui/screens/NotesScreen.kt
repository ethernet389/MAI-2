package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ethernet389.domain.model.note.Note
import com.ethernet389.mai.R
import com.ethernet389.mai.ui.components.ListGrid
import com.ethernet389.mai.ui.components.ListGridItem
import com.ethernet389.mai.ui.components.TextBody

@Composable
fun NotesScreen(
    notes: List<Note>,
    isList: Boolean = true,
    onDeleteClick: (Note) -> Unit,
    onShowClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    Divider(thickness = 1.dp)
    NoteListGrid(
        isList = isList,
        notes = notes,
        onDeleteClick = onDeleteClick,
        onShowClick = onShowClick,
        modifier = modifier
    )
}

@Composable
private fun NoteListGrid(
    isList: Boolean,
    notes: List<Note>,
    onDeleteClick: (Note) -> Unit,
    onShowClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    ListGrid(
        modifier = modifier,
        isList = isList,
        items = notes,
        listItem = { note ->
            NoteListGridItem(
                note = note,
                isList = true,
                onDeleteClick = onDeleteClick,
                onShowClick = onShowClick
            )
        },
        gridItem = { note ->
            NoteListGridItem(
                note = note,
                isList = false,
                onDeleteClick = onDeleteClick,
                onShowClick = onShowClick
            )
        },
        bottomContent = {
            Spacer(
                modifier = Modifier.height(100.dp)
            )
        }
    )
}

@Composable
private fun NoteListGridItem(
    note: Note,
    isList: Boolean,
    onDeleteClick: (Note) -> Unit,
    onShowClick: (Note) -> Unit,
    modifier: Modifier = Modifier
) {
    val prefix = if (note.alternatives.size > 1) {
        "1. "
    } else ""
    val expandedGridTemplateAndAlternatives = "${stringResource(R.string.template)}:" +
            "\n" +
            note.template.name +
            "\n\n" +
            "${stringResource(R.string.alternatives)}:" +
            "\n" +
            prefix + note.alternatives.reduceIndexed { index, acc, name ->
        acc + "\n${index + 1}. $name"
    }
    val foldedTemplateAndAlternatives = (if (isList) {
        "${stringResource(R.string.template)}: "
    } else "") + note.template.name

    val expandedListTemplateAndAlternatives = "${stringResource(R.string.template)}: " +
            note.template.name +
            "\n" +
            "${stringResource(R.string.alternatives)}: " +
            note.alternatives.reduce { acc, name ->
                "$acc, $name"
            }

    ListGridItem(
        title = note.name,
        body = TextBody(
            expandedBody = if (isList) {
                expandedListTemplateAndAlternatives
            } else {
                expandedGridTemplateAndAlternatives
            },
            foldedBody = foldedTemplateAndAlternatives
        ),
        onDeleteClick = { onDeleteClick(note) },
        onShowClick = { onShowClick(note) },
        modifier = modifier
    )
}