package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ethernet389.domain.model.note.Note
import com.ethernet389.mai.R
import com.ethernet389.mai.ui.components.ListGrid
import com.ethernet389.mai.ui.components.ListGridItem
import com.ethernet389.mai.ui.components.PreviewGrid
import com.ethernet389.mai.ui.components.PreviewList
import com.ethernet389.mai.ui.components.SupportScaffoldTitle
import com.ethernet389.mai.ui.components.TextBody

@Composable
fun NotesScreen(
    notes: List<Note>,
    isList: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SupportScaffoldTitle(text = stringResource(R.string.notes))
        Divider(thickness = 1.dp)
        NoteListGrid(
            isList = isList,
            notes = notes,
            modifier = Modifier.fillMaxSize()
        )
    }

}

@Composable
private fun NoteListGrid(
    isList: Boolean,
    notes: List<Note>,
    modifier: Modifier = Modifier
) {
    ListGrid(
        modifier = modifier,
        isList = isList,
        items = notes,
        listItem = { note ->
            NoteListGridItem(
                note = note,
                isList = true
            )
        },
        gridItem = { note ->
            NoteListGridItem(
                note = note,
                isList = false
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
        modifier = modifier
    )
}