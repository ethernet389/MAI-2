package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ethernet389.domain.model.template.Template
import com.ethernet389.mai.ui.components.ListGrid
import com.ethernet389.mai.ui.components.ListGridItem
import com.ethernet389.mai.ui.components.TextBody

@Composable
fun TemplatesScreen(
    templates: List<Template>,
    isList: Boolean = true,
    onDeleteClick: (Template) -> Unit,
    modifier: Modifier = Modifier
) {
    Divider(thickness = 1.dp)
    TemplateListGrid(
        isList = isList,
        onDeleteClick = onDeleteClick,
        templates = templates,
        modifier = modifier
    )
}


@Composable
private fun TemplateListGrid(
    isList: Boolean,
    onDeleteClick: (Template) -> Unit,
    templates: List<Template>,
    modifier: Modifier = Modifier
) {
    ListGrid(
        modifier = modifier,
        isList = isList,
        items = templates,
        listItem = { template ->
            TemplateListGridItem(template = template, isList = true, onDeleteClick = onDeleteClick)
        },
        gridItem = { template ->
            TemplateListGridItem(template = template, isList = false, onDeleteClick = onDeleteClick)
        },
        bottomContent = {
            Spacer(
                modifier = Modifier.height(100.dp)
            )
        }
    )
}


@Composable
private fun TemplateListGridItem(
    template: Template,
    modifier: Modifier = Modifier,
    onDeleteClick: (Template) -> Unit,
    isList: Boolean = true
) {
    val expandedCriteria = with(template.criteria) {
        val prefix = if (size > 1) {
            "1. "
        } else ""
        prefix + reduceIndexed { index, acc, item ->
            acc + "\n${index + 1}. $item"
        }
    }
    val foldedCriteria = if (isList) {
        template.criteria.reduce { acc, item ->
            "$acc, $item"
        }
    } else {
        template.criteria.first()
    }

    ListGridItem(
        title = template.name,
        body = TextBody(expandedBody = expandedCriteria, foldedBody = foldedCriteria),
        onDeleteClick = { onDeleteClick(template) },
        modifier = modifier
    )
}