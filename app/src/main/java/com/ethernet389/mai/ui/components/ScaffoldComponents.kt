package com.ethernet389.mai.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ethernet389.mai.R
import com.ethernet389.mai.ui.router.MaiScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleAppBar(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors()
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium
            )
        },
        scrollBehavior = scrollBehavior,
        colors = colors,
        modifier = modifier
    )
}

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    appScreens: Array<MaiScreen>,
    currentScreen: MaiScreen,
    onRouteClick: (MaiScreen) -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        appScreens.forEach { item ->
            NavigationBarItem(
                selected = currentScreen == item,
                onClick = { onRouteClick(item) },
                icon = {
                    Icon(
                        imageVector = item.navigationIcon,
                        contentDescription = stringResource(item.navigationTitle)
                    )
                },
                label = { Text(stringResource(item.navigationTitle)) }
            )
        }
    }
}

@Composable
fun AppFloatingActionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector?
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp),
        modifier = modifier
            .padding(3.dp)
            .size(64.dp)
    ) {
        AnimatedVisibility(
            visible = icon != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AnimatedContent(
                targetState = icon,
                label = "animate icon",
                transitionSpec = {
                    scaleIn(
                        animationSpec = tween(durationMillis = 300, easing = EaseInOutSine)
                    ).togetherWith(
                        scaleOut(
                            animationSpec = tween(durationMillis = 300, easing = EaseOutSine)
                        )
                    )
                }
            ) { newIcon ->
                Icon(
                    imageVector = newIcon ?: Icons.Outlined.AddBox,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}