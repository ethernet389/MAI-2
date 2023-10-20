package com.ethernet389.mai.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.ethernet389.mai.R
import com.ethernet389.mai.ui.components.RelationCard
import com.ethernet389.mai.ui.components.SupportScaffoldTitle
import com.ethernet389.mai.view_model.CreationNoteState

data class CardActions(
    val onArrowClick: (Int, Int, Int, Boolean) -> Unit,
    val onMinusClick: (Int, Int, Int, Double) -> Unit,
    val onPlusClick: (Int, Int, Int, Double) -> Unit
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateNoteScreen(
    noteName: String,
    creationNoteState: CreationNoteState,
    relationCardActions: CardActions,
    relationScale: ClosedFloatingPointRange<Double>,
    modifier: Modifier = Modifier
) {
    val noTemplateCriteriaComparison = creationNoteState.template.criteria.size == 1
    val comparisonPairs = with(creationNoteState) {
        when {
            noTemplateCriteriaComparison -> {
                listOf(template.criteria.first() to relationMatrices[1])
            }

            alternatives.size == 1 -> {
                    listOf(template.name to relationMatrices.first())
            }

            else -> {
                listOf(template.name to relationMatrices.first()) +
                        template.criteria.mapIndexed { i, criterion ->
                            criterion to relationMatrices[i + 1]
                        }
            }
        }
    }

    val pagerState = rememberPagerState { comparisonPairs.size }
    Column {
        SupportScaffoldTitle(text = noteName)
        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxSize()
        ) { pageIndex ->
            val currentNameList = when {
                pageIndex == 0 && !noTemplateCriteriaComparison ->
                    creationNoteState.template.criteria

                else -> creationNoteState.alternatives
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = comparisonPairs[pageIndex].first,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))
                    )
                }
                //TODO: Fix recomposition
                for (i in currentNameList.indices) {
                    for (j in (i + 1)..currentNameList.lastIndex) {
                        item {
                            var isInverse by rememberSaveable { mutableStateOf(false) }
                            val currentIndexes = if (isInverse) j to i else i to j
                            var currentValue by rememberSaveable {
                                mutableDoubleStateOf(comparisonPairs[pageIndex]
                                    .second[currentIndexes.first, currentIndexes.second])
                            }
                            RelationCard(
                                firstParameter = currentNameList[i],
                                secondParameter = currentNameList[j],
                                isInverse = isInverse,
                                relationValue = currentValue,
                                onArrowClick = {
                                    isInverse = !isInverse
                                    relationCardActions.onArrowClick(pageIndex, i, j, isInverse)
                                },
                                onMinusClick = {
                                    currentValue -= if (currentValue > relationScale.start) 1 else 0
                                    relationCardActions.onMinusClick(
                                        pageIndex,
                                        currentIndexes.first,
                                        currentIndexes.second,
                                        currentValue
                                    )
                                },
                                onPlusClick = {
                                    currentValue += if (currentValue < relationScale.endInclusive) 1 else 0
                                    relationCardActions.onPlusClick(
                                        pageIndex,
                                        currentIndexes.first,
                                        currentIndexes.second,
                                        currentValue
                                    )
                                }
                            )
                        }
                    }
                }
                /*itemsIndexed(currentNameList) { i, _ ->
                    for (j in (i + 1)..currentNameList.lastIndex) {
                        var isInverse by rememberSaveable { mutableStateOf(false) }
                        val currentFirstIndex = if (isInverse) j else i
                        val currentSecondIndex = if (isInverse) i else j
                        val currentValue = comparisonPairs[pageIndex]
                            .second[currentFirstIndex, currentSecondIndex]
                        RelationCard(
                            firstParameter = currentNameList[i],
                            secondParameter = currentNameList[j],
                            isInverse = isInverse,
                            relationValue = currentValue,
                            onArrowClick = {
                                isInverse = !isInverse
                                relationCardActions.onArrowClick(pageIndex, i, j, isInverse)
                            },
                            onMinusClick = {
                                relationCardActions.onMinusClick(
                                    pageIndex,
                                    currentFirstIndex,
                                    currentSecondIndex,
                                    currentValue - if (currentValue > relationScale.start) 1 else 0
                                )
                            },
                            onPlusClick = {
                                relationCardActions.onPlusClick(
                                    pageIndex,
                                    currentFirstIndex,
                                    currentSecondIndex,
                                    currentValue + if (currentValue < relationScale.endInclusive) 1 else 0
                                )
                            }
                        )
                    }
                }*/
            }
        }
    }
}