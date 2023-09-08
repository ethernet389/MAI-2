package com.ethernet389.mai.ui.theme

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Wysiwyg
import androidx.compose.ui.graphics.vector.ImageVector
import com.ethernet389.mai.R

enum class MaiScreen(
    @StringRes val navigationTitle: Int,
    val navigationIcon: ImageVector,
) {
    Notes(R.string.notes, Icons.Filled.Description),
    Templates(R.string.templates, Icons.Filled.Wysiwyg),
    Settings(R.string.settings, Icons.Filled.Engineering),
    Information(R.string.info, Icons.Filled.Help)
}