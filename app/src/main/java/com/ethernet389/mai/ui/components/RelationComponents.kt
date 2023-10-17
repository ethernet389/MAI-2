package com.ethernet389.mai.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ethernet389.mai.R

@Composable
fun RelationScale(
    currentValue: Double,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var previousValue by rememberSaveable {
        mutableDoubleStateOf(currentValue)
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(dimensionResource(R.dimen.little_padding))
            .clip(MaterialTheme.shapes.medium)
    ) {
        IconButton(
            onClick = {
                previousValue = currentValue
                onMinusClick()
            },
            modifier = Modifier.size(60.dp)
        ) {
            Icon(imageVector = Icons.Filled.Remove, contentDescription = null)
        }
        AnimatedContent(
            label = "Animate numeric change",
            targetState = currentValue,
            transitionSpec = {
                if (currentValue > previousValue) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                } using SizeTransform(clip = false)
            }
        ) {
            Text(
                text = it.toString(),
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        IconButton(
            onClick = {
                previousValue = currentValue
                onPlusClick()
            },
            modifier = Modifier.size(60.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
        }
    }
}

@Composable
fun ParameterToParameter(
    firstParameter: String,
    secondParameter: String,
    isInverse: Boolean,
    onArrowClick: () -> Unit,
    relationValue: Double,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val degrees by animateFloatAsState(
        targetValue = if (isInverse) 3 * 180f else 0f,
        label = "Rotate arrow on 3*180 degrees",
        animationSpec = tween(durationMillis = 200, easing = LinearEasing)
    )
    val color by animateColorAsState(
        targetValue = colorResource(
            if (isInverse) R.color.is_inverse_color else R.color.is_not_inverse_color
        ),
        label = "Change color to opposite",
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    )
    val inverseColor by animateColorAsState(
        targetValue = colorResource(
            if (!isInverse) R.color.is_inverse_color else R.color.is_not_inverse_color
        ),
        label = "Change color to opposite",
        animationSpec = tween(durationMillis = 300, easing = LinearEasing)
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = modifier
        ) {
            Text(
                text = firstParameter,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = color,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onArrowClick) {
                Icon(
                    imageVector = Icons.Filled.East,
                    contentDescription = null,
                    modifier = Modifier.rotate(degrees)
                )
            }
            Text(
                text = secondParameter,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = inverseColor,
                modifier = Modifier.weight(1f)
            )
        }
        RelationScale(
            currentValue = relationValue,
            onMinusClick = onMinusClick,
            onPlusClick = onPlusClick,
        )
    }
}

@Composable
fun RelationCard(
    firstParameter: String,
    secondParameter: String,
    relationValue: Double,
    isInverse: Boolean,
    onArrowClick: () -> Unit,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(dimensionResource(R.dimen.little_padding)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults
            .cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
    ) {
        ParameterToParameter(
            firstParameter = firstParameter,
            secondParameter = secondParameter,
            isInverse = isInverse,
            onArrowClick = onArrowClick,
            relationValue = relationValue,
            onMinusClick = onMinusClick,
            onPlusClick = onPlusClick,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.small_padding))
                .fillMaxWidth()
        )
    }
}