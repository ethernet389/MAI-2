package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.model.template.Template
import com.ethernet389.mai.ui.components.PreviewGrid
import com.ethernet389.mai.ui.components.PreviewList

@Composable
fun NotesScreen(
    notes: List<Note>,
    isList: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (isList) {
        PreviewList(
            items = notes,
            previewItem = { note ->
                ListItem(note = note)
            },
            modifier = modifier
        )
    } else {
        PreviewGrid(
            items = notes,
            previewItem = { note ->
                GridItem(note = note)
            },
            modifier = modifier
        )
    }
}
@Composable
private fun ListItem(
    note: Note,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = note.name, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview
@Composable
fun ListItemPreview() {
    ListItem(
        note = Note(
            1, "note", Template(1, "template", emptyList()), listOf("can1", "can2", "can3"), "report"
        )
    )
}

@Composable
private fun GridItem(
    note: Note,
    modifier: Modifier = Modifier
) {

}