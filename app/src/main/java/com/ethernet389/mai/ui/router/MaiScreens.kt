package com.ethernet389.mai.ui.router

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material.icons.outlined.NoteAdd
import androidx.compose.ui.graphics.vector.ImageVector
import com.ethernet389.mai.R

enum class MaiScreen(
    @StringRes val navigationTitle: Int,
    val navigationIcon: ImageVector,
    val fabIcon: ImageVector? = null,
    val isVisibleRoute: Boolean = true
) {
    Notes(R.string.notes, Icons.Outlined.Description, Icons.Outlined.NoteAdd),
    Templates(R.string.templates, Icons.Outlined.LibraryBooks, Icons.Outlined.LibraryAdd),
    Settings(R.string.settings, Icons.Outlined.Engineering),
    Information(R.string.info, Icons.Outlined.HelpOutline),
    CreateNotes(
        navigationTitle = R.string.note,
        navigationIcon = Icons.Outlined.NavigateNext,
        fabIcon = Icons.Outlined.NavigateNext,
        isVisibleRoute = false
    )
}