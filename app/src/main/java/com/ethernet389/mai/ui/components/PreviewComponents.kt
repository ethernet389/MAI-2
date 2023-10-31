package com.ethernet389.mai.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ethernet389.mai.R


@Composable
fun <T> ListGrid(
    isList: Boolean,
    items: List<T>,
    modifier: Modifier = Modifier,
    listItem: @Composable (T) -> Unit,
    gridItem: @Composable (T) -> Unit,
    cellMinWidth: Dp = dimensionResource(R.dimen.grid_item_min_size),
    bottomContent: @Composable () -> Unit = {}
) {
    if (isList) {
        PreviewList(
            items = items,
            previewItem = { listItem(it) },
            bottomContent = bottomContent,
            modifier = modifier
        )
    } else {
        PreviewGrid(
            items = items,
            previewItem = { gridItem(it) },
            bottomContent = bottomContent,
            minSize = cellMinWidth,
            modifier = modifier
        )
    }
}

data class TextBody(val expandedBody: String, val foldedBody: String)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun ListGridItem(
    title: String,
    body: TextBody,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Card(
        elevation = CardDefaults
            .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        modifier = modifier
            .padding(dimensionResource(R.dimen.little_padding)),
        shape = MaterialTheme.shapes.medium,
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(animationSpec = tween(durationMillis = 300))
                .padding(dimensionResource(R.dimen.medium_padding))
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (isExpanded) body.expandedBody else body.foldedBody,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.small_padding))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(R.dimen.small_padding)),
                contentAlignment = Alignment.CenterEnd
            ) {
                IconButton(onClick = onDeleteClick, modifier = Modifier.size(30.dp)) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun <T> PreviewGrid(
    items: List<T>,
    previewItem: @Composable (T) -> Unit,
    bottomContent: @Composable () -> Unit = {},
    minSize: Dp = dimensionResource(R.dimen.grid_item_min_size),
    modifier: Modifier = Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = minSize),
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        items(items) {
            previewItem(it)
        }
        item {
            bottomContent()
        }
    }
}

@Composable
fun <T> PreviewList(
    items: List<T>,
    previewItem: @Composable (T) -> Unit,
    bottomContent: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        items(items = items) {
            previewItem(it)
        }
        item {
            bottomContent()
        }
    }
}
