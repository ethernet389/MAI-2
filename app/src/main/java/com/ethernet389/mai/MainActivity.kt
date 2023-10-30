package com.ethernet389.mai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ethernet389.mai.ui.components.AppFloatingActionButton
import com.ethernet389.mai.ui.components.NavigationBottomBar
import com.ethernet389.mai.ui.components.dialogs.NoteCreationDialog
import com.ethernet389.mai.ui.components.dialogs.TemplateCreationDialog
import com.ethernet389.mai.ui.components.TitleAppBar
import com.ethernet389.mai.ui.components.dialogs.ConfirmationDeletionDialog
import com.ethernet389.mai.ui.router.MaiScreen
import com.ethernet389.mai.ui.screens.CardActions
import com.ethernet389.mai.ui.screens.CreateNoteScreen
import com.ethernet389.mai.ui.screens.InfoScreen
import com.ethernet389.mai.ui.screens.NotesScreen
import com.ethernet389.mai.ui.screens.ResultScreen
import com.ethernet389.mai.ui.screens.SettingsScreen
import com.ethernet389.mai.ui.screens.TemplatesScreen
import com.ethernet389.mai.ui.theme.MAITheme
import com.ethernet389.mai.util.spannableStringToAnnotatedString
import com.ethernet389.mai.view_model.MaiViewModel
import com.ethernet389.mai.view_model.relationScale
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
    //UI state and current creation note state
    val uiState by viewModel.uiStateFlow.collectAsState()
    val creationNoteState by viewModel.creationNoteState.collectAsState()

    //Current navigation screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val screenArray = MaiScreen.values()
    val currentScreen = screenArray.find {
        currentDestination?.route == it.name
    }
    val fabVisible = currentScreen?.fabIcon != null
    val listGridSwitchVisible = when (currentScreen) {
        MaiScreen.Templates, MaiScreen.Notes -> true
        else -> false
    }

    //Folded scroll behavior
    val scrollBehavior = TopAppBarDefaults
        .enterAlwaysScrollBehavior(rememberTopAppBarState())

    //List or Grid view
    var listOn by rememberSaveable { mutableStateOf(true) }
    //Note/Template creation dialog
    var showCreationDialog by rememberSaveable { mutableStateOf(false) }
    //Confirm deletion dialog
    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }

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
                            viewModel.postNoteFromCreationNoteState()
                            viewModel.dropCreationNoteState()
                            navController.navigate(route = MaiScreen.Notes.name)
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
                NotesScreen(
                    notes = uiState.notes,
                    isList = listOn
                )
                if (showCreationDialog) {
                    NoteCreationDialog(
                        onDismissRequest = { showCreationDialog = false },
                        onCreateRequest = { noteName, chosenTemplate, alternatives ->
                            val template = uiState.templates.find { it.id == chosenTemplate.id }!!
                            //Show error message when number of alternatives = 1 and number criteria = 1
                            if (alternatives.size <= 1 && template.criteria.size <= 1) {
                                return@NoteCreationDialog
                            }
                            viewModel.createNewCreationNoteState(noteName, template, alternatives)
                            navController.navigate(MaiScreen.CreateNotes.name)
                            showCreationDialog = false
                        },
                        templates = uiState.templates,
                        modifier = Modifier.verticalScroll(state = rememberScrollState())
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
                        },
                        modifier = Modifier.verticalScroll(state = rememberScrollState())
                    )
                }
            }
            composable(route = MaiScreen.Settings.name) {
                val resources = LocalContext.current.resources
                val density = LocalDensity.current

                var text by remember {
                    mutableStateOf(AnnotatedString(""))
                }
                var lambda by remember {
                    mutableStateOf({})
                }
                val updateAndShow = { id: Int, func: () -> Unit ->
                    text = spannableStringToAnnotatedString(
                        resources.getText(id),
                        density
                    )
                    lambda = func
                    showDeletionDialog = true
                }
                SettingsScreen(
                    onDeleteNotesClick = {
                        updateAndShow(R.string.delete_all_notes_warning) {
                            viewModel.deleteAllNotes()
                        }
                    },
                    onDeleteUnusedTemplatesClick = {
                        updateAndShow(R.string.delete_all_unused_template_warning)
                        { viewModel.deleteUnusedTemplates() }
                    },
                    onClearAllDatabaseClick = {
                        updateAndShow(R.string.delete_all_data_warning) {
                            viewModel.clearAllData()
                        }
                    }
                )
                if (showDeletionDialog) {
                    ConfirmationDeletionDialog(
                        onDismissRequest = { showDeletionDialog = false },
                        onConfirmRequest = {
                            lambda()
                            showDeletionDialog = false
                        },
                        text = text
                    )
                }
            }
            composable(route = MaiScreen.Information.name) { InfoScreen() }
            composable(route = MaiScreen.CreateNotes.name) {
                val noTemplateCriteriaComparison = creationNoteState.template.criteria.size == 1
                val noCandidatesComparison = creationNoteState.alternatives.size == 1
                if (noTemplateCriteriaComparison && noCandidatesComparison) {
                    return@composable
                }
                val cardActions = CardActions(
                    onPlusClick = { pageIndex, currentFirst, currentSecond, newValue ->
                        viewModel.updateMatrixByIndex(
                            pageIndex, currentFirst, currentSecond, newValue
                        )
                    },
                    onMinusClick = { pageIndex, currentFirst, currentSecond, newValue ->
                        viewModel.updateMatrixByIndex(
                            pageIndex, currentFirst, currentSecond, newValue
                        )
                    },
                    onArrowClick = { pageIndex, i, j, isInverse ->
                        if (isInverse) {
                            viewModel.swapValuesMatrix(pageIndex, i, j)
                        } else {
                            viewModel.swapValuesMatrix(pageIndex, j, i)
                        }

                    }
                )
                CreateNoteScreen(
                    noteName = creationNoteState.noteName,
                    creationNoteState = creationNoteState,
                    relationCardActions = cardActions,
                    relationScale = relationScale
                )
            }
            composable(
                route = "${MaiScreen.Result.name}/{note_id}",
                arguments = listOf(navArgument("note_id") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("note_id")!!
                val note = uiState.notes.find { note -> noteId == note.id }!!
                ResultScreen(note = note)
            }
        }
    }
}