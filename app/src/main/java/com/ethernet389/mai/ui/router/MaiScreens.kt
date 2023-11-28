package com.ethernet389.mai.ui.router

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LibraryAdd
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.NoteAdd
import androidx.compose.ui.graphics.vector.ImageVector
import com.ethernet389.mai.R

//Each name doesn't contains other name (Unique routes) else app doesn't work correctly
enum class MaiScreen(
    @StringRes val navigationTitle: Int,
    val navigationIcon: ImageVector,
    val fabIcon: ImageVector? = null,
    val isVisibleRoute: Boolean = true
) {
    Notes(R.string.notes, Icons.Outlined.Description, Icons.Outlined.NoteAdd),
    Templates(R.string.templates, Icons.Outlined.LibraryBooks, Icons.Outlined.LibraryAdd),
    Settings(R.string.settings, Icons.Outlined.Engineering),
    Information(R.string.information, Icons.Outlined.HelpOutline),
    CreateNote(
        navigationTitle = R.string.create_note,
        navigationIcon = Icons.Outlined.Check,
        fabIcon = Icons.Outlined.Check,
        isVisibleRoute = false
    ),
    Result(
        navigationTitle = R.string.result,
        navigationIcon = Icons.Outlined.Done,
        isVisibleRoute = false
    )
}