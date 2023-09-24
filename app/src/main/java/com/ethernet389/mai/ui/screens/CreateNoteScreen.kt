package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ethernet389.domain.model.template.Template

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateNoteScreen(
    noteName: String,
    template: Template,
    alternatives: List<String>,
    modifier: Modifier = Modifier
) {
    val inputPagerState = rememberPagerState(initialPage = 1) {
        10
    }
    HorizontalPager(state = inputPagerState) {

    }
}