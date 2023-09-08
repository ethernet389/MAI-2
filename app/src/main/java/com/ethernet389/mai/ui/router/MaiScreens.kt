package com.ethernet389.mai.ui.router

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Wysiwyg
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.ethernet389.mai.R

enum class MaiScreen(
    @StringRes val navigationTitle: Int,
    val navigationIcon: ImageVector,
) {
    Notes(R.string.notes, Icons.Outlined.Description),
    Templates(R.string.templates, Icons.Filled.Wysiwyg),
    Settings(R.string.settings, Icons.Filled.Engineering),
    Information(R.string.info, Icons.Outlined.HelpOutline)
}