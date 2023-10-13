package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.ethernet389.domain.model.template.Template
import com.ethernet389.mai.R
import com.ethernet389.mai.ui.components.RelationCard
import com.ethernet389.mai.ui.components.SupportScaffoldTitle

val relationScale = 1..9

data class RelationInfo(
    val firstParameter: String,
    val secondParameter: String,
    val relationValue: Int = 1,
    val isPlaceChanged: Boolean = true
)

data class RelationInfoMassive(
    val relationName: String,
    val relations: List<RelationInfo>
)

private fun getAllRelationsCombinations(relationMembers: List<String>): List<RelationInfo> {
    val relations = mutableListOf<RelationInfo>()
    for (i in relationMembers.indices) {
        val first = relationMembers[i]
        for (j in (i + 1)..relationMembers.lastIndex) {
            val second = relationMembers[j]
            relations.add(RelationInfo(first, second))
        }
    }
    return relations
}

private fun getAllRelations(
    template: Template,
    alternatives: List<String>
): List<RelationInfoMassive> {
    val relations = mutableListOf<RelationInfoMassive>()
    //Template base relations
    val templateRelations = getAllRelationsCombinations(relationMembers = template.criteria)
    if (templateRelations.isNotEmpty()) {
        relations.add(
            RelationInfoMassive(
                relationName = template.name,
                relations = templateRelations
            )
        )
    }
    //Alternatives base relations
    val alternativesRelations = getAllRelationsCombinations(relationMembers = alternatives)
    if (alternativesRelations.isNotEmpty()) {
        for (criterion in template.criteria) {
            val relationMassive = RelationInfoMassive(
                relationName = criterion, relations = alternativesRelations
            )
            relations.add(relationMassive)
        }
    }
    return relations
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateNoteScreen(
    noteName: String,
    template: Template,
    alternatives: List<String>,
    modifier: Modifier = Modifier
) {
    val relations = remember {
        mutableStateListOf<RelationInfoMassive>().apply {
            getAllRelations(template, alternatives).forEach { item -> this.add(item) }
        }
    }
    val pagerState = rememberPagerState { relations.size }
    Column {
        SupportScaffoldTitle(text = noteName)
        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxSize()
        ) {i ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = relations[i].relationName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))
                    )
                }
                itemsIndexed(relations[i].relations) { j, info ->
                    val mutableRelationsInfo = relations[i].relations.toMutableList()
                    RelationCard(
                        firstParameter = info.firstParameter,
                        secondParameter = info.secondParameter,
                        isPlaceChanged = info.isPlaceChanged,
                        onArrowClick = {
                            relations[i] = relations[i].copy(
                                relations = mutableRelationsInfo.apply {
                                    this[j] = this[j].copy(isPlaceChanged = !this[j].isPlaceChanged)
                                }
                            )
                        },
                        relationValue = info.relationValue,
                        onMinusClick = {
                            relations[i] = relations[i].copy(
                                relations = mutableRelationsInfo.apply {
                                    this[j] = this[j].copy(
                                        relationValue = with(this[j].relationValue) {
                                            this - if (this > relationScale.first) 1 else 0
                                        }
                                    )
                                }
                            )
                        },
                        onPlusClick = {
                            relations[i] = relations[i].copy(
                                relations = mutableRelationsInfo.apply {
                                    this[j] = this[j].copy(
                                        relationValue = with(this[j].relationValue) {
                                            this + if (this < relationScale.last) 1 else 0
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreateNoteScreenPreview() {
    CreateNoteScreen(
        noteName = "NoteTest",
        template = Template(
            name = "TemplateTest",
            criteria = listOf("Test1", "Test2", "Test3", "Test4")
        ),
        alternatives = listOf("Alt1", "Alt2", "Alt3", "Alt4")
    )
}