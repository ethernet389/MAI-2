package com.ethernet389.mai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ethernet389.mai.ui.components.AppFloatingActionButton
import com.ethernet389.mai.ui.components.NavigationBottomBar
import com.ethernet389.mai.ui.components.NoteCreationDialog
import com.ethernet389.mai.ui.components.TemplateCreationDialog
import com.ethernet389.mai.ui.components.TitleAppBar
import com.ethernet389.mai.ui.router.MaiScreen
import com.ethernet389.mai.ui.screens.CardActions
import com.ethernet389.mai.ui.screens.CreateNoteScreen
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
    //UI state and creation note state
    val uiState by viewModel.uiStateFlow.collectAsState()
    val creationNoteState by viewModel.creationNoteState.collectAsState()

    //Current navigation screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val screenArray = MaiScreen.values()
    //Use split because "${MaiScreen.CreateNotes.name}/{note_name}/{template_id}/{alternatives}"
    //Deep links no needs
    val currentScreen = screenArray.find {
        val routeName = currentDestination?.route?.split("/")?.first()
        routeName == it.name
    }
    val fabVisible = currentScreen?.fabIcon != null
    val listGridSwitchVisible = when (currentScreen) {
        MaiScreen.Templates, MaiScreen.Notes -> true
        else -> false
    }

    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())

    //List or Grid view
    var listOn by rememberSaveable {
        mutableStateOf(true)
    }
    //Note/Template creation dialog
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
                listGridSwitchVisible = listGridSwitchVisible,
                listOn = listOn,
                onListGridSwitchClick = {
                    listOn = !listOn
                }
            )
        },
        bottomBar = {
            NavigationBottomBar(
                appScreens = screenArray,
                currentScreen = currentScreen,
                onRouteIconClick = { newScreen ->
                    navController.navigate(route = newScreen.name)
                }
            )
        },
        floatingActionButton = {
            AppFloatingActionButton(
                icon = currentScreen?.fabIcon,
                visible = fabVisible,
                onClick = {
                    when (currentScreen) {
                        MaiScreen.Notes, MaiScreen.Templates -> showCreationDialog = true
                        MaiScreen.CreateNotes -> {
                            creationNoteState.relationList
                            viewModel.dropCreationNoteState()
                        }

                        else -> {}
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
                if (showCreationDialog) {
                    NoteCreationDialog(
                        onDismissRequest = { showCreationDialog = false },
                        onCreateRequest = { noteName, chosenTemplate, alternatives ->
                            val template = uiState.templates.find { it.id == chosenTemplate.id }!!
                            if (alternatives.size <= 1 && template.criteria.size <= 1) {
                                return@NoteCreationDialog
                            }
                            viewModel.updateCreationNoteState(noteName, template, alternatives)
                            navController.navigate(MaiScreen.CreateNotes.name)
                            showCreationDialog = false
                        },
                        templates = uiState.templates
                    )
                }
            }
            composable(route = MaiScreen.Templates.name) {
                TemplatesScreen(templates = uiState.templates, isList = listOn)
                if (showCreationDialog) {
                    TemplateCreationDialog(
                        onDismissRequest = { showCreationDialog = false },
                        onCreateRequest = { newTemplate ->
                            viewModel.createTemplate(newTemplate)
                            showCreationDialog = false
                        }
                    )
                }
            }
            composable(route = MaiScreen.Settings.name) { SettingsScreen() }
            composable(route = MaiScreen.Information.name) { InfoScreen() }
            composable(route = MaiScreen.CreateNotes.name) {
                val cardActions = CardActions(
                    onArrowClick = { i, j, newRelationInfo ->
                        viewModel.updateCreationNoteState(i, j, newRelationInfo)
                    },
                    onMinusClick = { i, j, newRelationInfo ->
                        viewModel.updateCreationNoteState(i, j, newRelationInfo)
                    },
                    onPlusClick = { i, j, newRelationInfo ->
                        viewModel.updateCreationNoteState(i, j, newRelationInfo)
                    }
                )
                CreateNoteScreen(
                    noteName = creationNoteState.noteName,
                    relationsMassive = creationNoteState.relationList,
                    relationCardActions = cardActions
                )
            }
        }
    }
}