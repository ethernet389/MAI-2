package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ethernet389.domain.model.template.Template
import com.ethernet389.mai.R
import com.ethernet389.mai.ui.components.ListGrid
import com.ethernet389.mai.ui.components.ListGridItem
import com.ethernet389.mai.ui.components.TextBody

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
    ListGrid(
        modifier = modifier,
        isList = isList,
        items = templates,
        listItem = { template ->
            TemplateListGridItem(template = template)
        },
        gridItem = { template ->
            TemplateListGridItem(template = template)
        }
    )
}



@Composable
private fun TemplateListGridItem(
    template: Template,
    modifier: Modifier = Modifier
) {
    val expandedCriteria = "1. " + template.criteria.reduceIndexed { index, acc, item ->
        acc + "\n${index + 1}. $item"
    }
    val foldedCriteria = template.criteria.reduce { acc, item ->
        "$acc, $item"
    }

    ListGridItem(
        title = template.name,
        body = TextBody(expandedBody = expandedCriteria, foldedBody = foldedCriteria),
        modifier = modifier
    )
}

@Composable
private fun GridItem(
    template: Template,
    modifier: Modifier = Modifier
) {
    Card {

    }
}