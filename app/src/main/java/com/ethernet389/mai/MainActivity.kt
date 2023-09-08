package com.ethernet389.mai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ethernet389.mai.ui.components.AppFloatingActionButton
import com.ethernet389.mai.ui.components.NavigationBottomBar
import com.ethernet389.mai.ui.components.TitleAppBar
import com.ethernet389.mai.ui.router.MaiScreen
import com.ethernet389.mai.ui.screens.InfoScreen
import com.ethernet389.mai.ui.screens.NotesScreen
import com.ethernet389.mai.ui.screens.SettingsScreen
import com.ethernet389.mai.ui.screens.TemplatesScreen
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
    val currentDestination = backStackEntry?.destination
    val screenArray = MaiScreen.values()
    val currentScreen = screenArray.find {
        it.name == currentDestination?.route
    }

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
            NavigationBottomBar(
                appScreens = screenArray,
                currentScreen = currentScreen ?: MaiScreen.Templates,
                onRouteClick = { newScreen ->
                    navController.navigate(route = newScreen.name)
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = currentScreen?.fabIcon != null,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ),
                exit = scaleOut()
            ) {
                AppFloatingActionButton(
                    icon = currentScreen?.fabIcon,
                    onClick = {},
                )
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MaiScreen.Templates.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = MaiScreen.Notes.name) { NotesScreen() }
            composable(route = MaiScreen.Templates.name) { TemplatesScreen() }
            composable(route = MaiScreen.Settings.name) { SettingsScreen() }
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