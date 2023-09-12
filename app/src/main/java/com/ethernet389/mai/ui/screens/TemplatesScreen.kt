package com.ethernet389.mai.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseInElastic
import androidx.compose.animation.core.EaseInExpo
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ethernet389.domain.model.template.Template
import com.ethernet389.mai.R
import com.ethernet389.mai.ui.components.PreviewGrid
import com.ethernet389.mai.ui.components.PreviewList

@Composable
fun TemplatesScreen(
    templates: List<Template>,
    isList: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Text(
                text = stringResource(R.string.templates),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp, top = 2.dp)
            )
        }
        Divider(thickness = 1.dp)
        TemplateListGrid(isList = isList, templates = templates, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun TemplateListGrid(
    isList: Boolean,
    templates: List<Template>,
    modifier: Modifier = Modifier
) {
    if (isList) {
        PreviewList(
            items = templates,
            previewItem = { template ->
                ListItem(template = template)
            },
            modifier = modifier
        )
    } else {
        PreviewGrid(
            items = templates,
            previewItem = { template ->
                ListItem(template = template)
            },
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListItem(
    template: Template,
    modifier: Modifier = Modifier
) {
    val expandedCriteria = "1. " + template.criteria.reduceIndexed { index, acc, item ->
        acc + "\n${index + 1}. $item"
    }
    val foldedCriteria = template.criteria.reduce { acc, item ->
        "$acc, $item"
    }

    var isExpanded by rememberSaveable { mutableStateOf(false) }
    Card(
        elevation = CardDefaults
            .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        modifier = modifier.padding(dimensionResource(R.dimen.little_padding)),
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
                text = template.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = if (isExpanded) expandedCriteria else foldedCriteria,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                modifier = Modifier.padding(start = dimensionResource(R.dimen.small_padding))
            )
        }
    }
}

@Composable
private fun GridItem(
    template: Template,
    modifier: Modifier = Modifier
) {
    Card {

    }
}