package com.ethernet389.mai.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.ethernet389.mai.R
import com.ethernet389.mai.ui.components.RelationCard
import com.ethernet389.mai.ui.components.SupportScaffoldTitle
import com.ethernet389.mai.view_model.RelationInfo
import com.ethernet389.mai.view_model.RelationInfoMassive
import com.ethernet389.mai.view_model.relationScale

data class CardActions(
    val onArrowClick: (Int, Int, RelationInfo) -> Unit,
    val onMinusClick: (Int, Int, RelationInfo) -> Unit,
    val onPlusClick: (Int, Int, RelationInfo) -> Unit
)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateNoteScreen(
    noteName: String,
    relationsMassive: List<RelationInfoMassive>,
    relationCardActions: CardActions,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState { relationsMassive.size }
    Column {
        SupportScaffoldTitle(text = noteName)
        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxSize()
        ) { pageIndex ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = relationsMassive[pageIndex].relationName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding))
                    )
                }
                //TODO: Find clearer way to update data
                itemsIndexed(relationsMassive[pageIndex].relations) { itemIndex, info ->
                    RelationCard(
                        firstParameter = info.firstParameter,
                        secondParameter = info.secondParameter,
                        isPlaceChanged = info.isInverse,
                        onArrowClick = {
                            val newRelationInfo = info.copy(isInverse =  !info.isInverse)
                            relationCardActions.onArrowClick(pageIndex, itemIndex,newRelationInfo)
                        },
                        relationValue = info.relationValue,
                        onMinusClick = {
                            val newRelationValue = info.relationValue - when {
                                info.relationValue > relationScale.first -> 1
                                else -> 0
                            }
                            val newRelationInfo = info.copy(relationValue = newRelationValue)
                            relationCardActions.onMinusClick(pageIndex, itemIndex,newRelationInfo)
                        },
                        onPlusClick = {
                            val newRelationValue = info.relationValue + when {
                                info.relationValue < relationScale.last -> 1
                                else -> 0
                            }
                            val newRelationInfo = info.copy(relationValue = newRelationValue)
                            relationCardActions.onPlusClick(pageIndex, itemIndex,newRelationInfo)
                        }
                    )
                }
            }
        }
    }
}