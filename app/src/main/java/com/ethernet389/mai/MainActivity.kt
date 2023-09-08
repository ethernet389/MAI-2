package com.ethernet389.mai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ethernet389.mai.ui.router.MaiScreen
import com.ethernet389.mai.ui.screens.InfoScreen
import com.ethernet389.mai.ui.theme.MAITheme
import com.ethernet389.mai.view_model.MaiViewModel
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAITheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MaiApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaiApp(
    viewModel: MaiViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            TitleAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults
                    .centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
            )
        },
        bottomBar = {
            val screenArray = MaiScreen.values()
            NavigationBottomBar(
                appScreens = screenArray,
                currentScreen = screenArray.find {
                    it.name == backStackEntry?.destination?.route
                } ?: MaiScreen.Templates,
                onRouteClick = { newScreen ->
                    navController.navigate(route = newScreen.name)
                }
            )
        },
        floatingActionButton = {
            AppFloatingActionButton {

            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MaiScreen.Templates.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = MaiScreen.Notes.name) { Text(text = "I'm notes!") }
            composable(route = MaiScreen.Templates.name) { Text(text = "I'm templates!") }
            composable(route = MaiScreen.Settings.name) { Text(text = "I'm settings!") }
            composable(route = MaiScreen.Information.name) { InfoScreen() }
            composable(
                route = "${MaiScreen.Notes.name}/{note_id}",
                arguments = listOf(navArgument("note_id") { type = NavType.IntType })
            ) { navBackStackEntry ->
                val txt = navBackStackEntry.arguments?.getInt("note_id")
                Text("I'm note with id: $txt")
            }
            composable(
                route = "${MaiScreen.Templates.name}/{template_id}",
                arguments = listOf(navArgument("template_id") { type = NavType.IntType })
            ) { backStackEntry ->
                val txt = backStackEntry.arguments?.getInt("template_id")
                Text("I'm template with id: $txt")
            }
        }
    }
}

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
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = modifier
            .padding(3.dp)
            .size(64.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}