package com.ethernet389.mai.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ethernet389.domain.model.note.Note

@Composable
fun ResultScreen(
    note: Note,
    modifier: Modifier = Modifier
) {
    Text(text = note.report)
}