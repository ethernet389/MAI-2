package com.ethernet389.mai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ethernet389.domain.model.template.Template
import com.ethernet389.mai.ui.components.AppFloatingActionButton
import com.ethernet389.mai.ui.components.CreationDialog
import com.ethernet389.mai.ui.components.NavigationBottomBar
import com.ethernet389.mai.ui.components.TitleAppBar
import com.ethernet389.mai.ui.router.MaiScreen
import com.ethernet389.mai.ui.screens.InfoScreen
import com.ethernet389.mai.ui.screens.NotesScreen
import com.ethernet389.mai.ui.screens.SettingsScreen
import com.ethernet389.mai.ui.screens.TemplatesScreen
import com.ethernet389.mai.ui.theme.MAITheme
import com.ethernet389.mai.view_model.MaiViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAITheme {
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
    //UI state
    val uiState by viewModel.uiStateFlow.collectAsState()

    //Current navigation screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val screenArray = MaiScreen.values()
    val currentScreen = screenArray.find {
        it.name == currentDestination?.route
    }
    val creationVisible = currentScreen?.fabIcon != null

    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())

    //List or Grid view
    var listOn by rememberSaveable {
        mutableStateOf(true)
    }
    //Note creation dialog
    var showCreationDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TitleAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults
                    .centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                listGridSwitchVisible = creationVisible,
                listOn = listOn,
                onListGridSwitchClick = {
                    listOn = !listOn
                }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                appScreens = screenArray,
                currentScreen = currentScreen ?: MaiScreen.Templates,
                onRouteIconClick = { newScreen ->
                    navController.navigate(route = newScreen.name)
                }
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                icon = currentScreen?.fabIcon,
                visible = creationVisible,
                onClick = {
                    when(currentScreen?.name) {
                        MaiScreen.Templates.name -> showCreationDialog = true
                    }
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = MaiScreen.Templates.name,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = MaiScreen.Notes.name) {
                NotesScreen(notes = uiState.notes, isList = listOn)
            }
            composable(route = MaiScreen.Templates.name) {
                val errorMessage = stringResource(R.string.empty_fields_error)
                TemplatesScreen(
                    templates = uiState.templates,
                    isList = listOn,
                    dialogContent = {
                        if (showCreationDialog) {
                            CreationDialog(
                                onDismissRequest = { showCreationDialog = false },
                                onCreateRequest = { name, criteria ->
                                    val newTemplate = Template(name = name, criteria = criteria)
                                    viewModel.createTemplate(newTemplate)
                                    showCreationDialog = false
                                },
                                title = stringResource(R.string.template),
                                namePlaceholder = stringResource(R.string.name_example),
                                optionsPlaceholder = stringResource(R.string.criteria_example),
                                optionsLabel = stringResource(R.string.criteria)
                            )
                        }
                    }
                )
            }
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