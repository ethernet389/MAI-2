package com.ethernet389.mai

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseInSine
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ethernet389.mai.ui.components.AppFloatingActionButton
import com.ethernet389.mai.ui.components.NavigationBottomBar
import com.ethernet389.mai.ui.components.SupportScaffoldTitle
import com.ethernet389.mai.ui.components.TitleAppBar
import com.ethernet389.mai.ui.components.dialogs.ConfirmationDeletionDialog
import com.ethernet389.mai.ui.components.dialogs.NoteCreationDialog
import com.ethernet389.mai.ui.components.dialogs.TemplateCreationDialog
import com.ethernet389.mai.ui.router.MaiScreen
import com.ethernet389.mai.ui.screens.CardActions
import com.ethernet389.mai.ui.screens.CreateNoteScreen
import com.ethernet389.mai.ui.screens.InfoScreen
import com.ethernet389.mai.ui.screens.NotesScreen
import com.ethernet389.mai.ui.screens.ResultScreen
import com.ethernet389.mai.ui.screens.SettingsScreen
import com.ethernet389.mai.ui.screens.TemplatesScreen
import com.ethernet389.mai.ui.theme.MAITheme
import com.ethernet389.mai.util.annotatedStringResource
import com.ethernet389.mai.util.spannableStringToAnnotatedString
import com.ethernet389.mai.view_model.MaiViewModel
import com.ethernet389.mai.view_model.states.relationScale
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
    //UI and MAI state and current creation note state
    val uiState by viewModel.uiStateFlow.collectAsState()
    val maiState by viewModel.maiResultNoteStateFlow.collectAsState()
    val creationNoteState by viewModel.creationNoteState.collectAsState()

    //Annotated string resources
    val resources = LocalContext.current.resources
    val density = LocalDensity.current

    //Current navigation screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val screenArray = MaiScreen.values()
    val currentScreen = screenArray.find {
        currentDestination?.route?.contains(it.name) ?: false
    }
    val fabVisible = currentScreen?.fabIcon != null
    val listGridSwitchVisible = when (currentScreen) {
        MaiScreen.Templates, MaiScreen.Notes -> true
        else -> false
    }
    val supportScaffoldTitle = stringResource(currentScreen?.navigationTitle ?: R.string._null)

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
            Column {
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
                SupportScaffoldTitle(text = supportScaffoldTitle)
            }
        },
        bottomBar = {
            NavigationBottomBar(
                appScreens = screenArray,
                currentScreen = currentScreen,
                onRouteIconClick = { newScreen ->
                    if (newScreen != currentScreen) {
                        navController.navigate(route = newScreen.name)
                    }
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
                        MaiScreen.CreateNote -> {
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
            animatedComposable(route = MaiScreen.Notes.name) {
                var lambda by remember { mutableStateOf({}) }
                NotesScreen(
                    notes = uiState.notes,
                    isList = listOn,
                    onDeleteClick = { note ->
                        lambda = { viewModel.deleteNote(note) }
                        showDeletionDialog = true
                    },
                    onShowClick = { note ->
                        viewModel.setNewCurrentMaiNoteState(note)
                        navController.navigate(route = "${MaiScreen.Result.name}/${note.id}")
                    }
                )
                if (showDeletionDialog) {
                    ConfirmationDeletionDialog(
                        onDismissRequest = { showDeletionDialog = false },
                        onConfirmRequest = {
                            lambda()
                            showDeletionDialog = false
                        },
                        text = annotatedStringResource(R.string.delete_note_warning)
                    )
                } else if (showCreationDialog) {
                    val context = LocalContext.current
                    val text = stringResource(
                        R.string.unique_constraint_error,
                        stringResource(R.string.note)
                    )
                    NoteCreationDialog(
                        onDismissRequest = { showCreationDialog = false },
                        onCreateRequest = { noteName, chosenTemplate, alternatives ->
                            //All names should be unique!
                            if (uiState.notes.any { it.name == noteName }) {
                                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                                return@NoteCreationDialog
                            }
                            val template = uiState.templates.find { it.id == chosenTemplate.id }!!
                            //Show error message when number of alternatives = 1 and number criteria = 1
                            if (alternatives.size <= 1 && template.criteria.size <= 1) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.template_and_note_incompatibility_error),
                                    Toast.LENGTH_LONG
                                ).show()
                                return@NoteCreationDialog
                            }
                            viewModel.createNewCreationNoteState(noteName, template, alternatives)
                            navController.navigate(MaiScreen.CreateNote.name)
                            showCreationDialog = false

                        },
                        templates = uiState.templates,
                        modifier = Modifier.verticalScroll(state = rememberScrollState())
                    )
                }
            }
            animatedComposable(route = MaiScreen.Templates.name) {
                var lambda by remember { mutableStateOf({}) }
                TemplatesScreen(
                    templates = uiState.templates,
                    isList = listOn,
                    onDeleteClick = { template ->
                        lambda = { viewModel.deleteTemplate(template) }
                        showDeletionDialog = true
                    }
                )
                if (showDeletionDialog) {
                    ConfirmationDeletionDialog(
                        onDismissRequest = { showDeletionDialog = false },
                        onConfirmRequest = {
                            lambda()
                            showDeletionDialog = false
                        },
                        text = annotatedStringResource(R.string.delete_template_warning)
                    )
                } else if (showCreationDialog) {
                    val context = LocalContext.current
                    val text = stringResource(
                        R.string.unique_constraint_error,
                        stringResource(R.string.template)
                    )
                    TemplateCreationDialog(
                        onDismissRequest = { showCreationDialog = false },
                        onCreateRequest = { newTemplate ->
                            //All names should be unique!
                            if (uiState.templates.any { it.name == newTemplate.name }) {
                                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.createTemplate(newTemplate)
                                showCreationDialog = false
                            }
                        },
                        modifier = Modifier.verticalScroll(state = rememberScrollState())
                    )
                }
            }
            animatedComposable(route = MaiScreen.Settings.name) {
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
                        updateAndShow(R.string.delete_all_unused_template_warning) {
                            viewModel.deleteUnusedTemplates()
                        }
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
            animatedComposable(route = MaiScreen.Information.name) { InfoScreen() }
            animatedComposable(route = MaiScreen.CreateNote.name) {
                val noTemplateCriteriaComparison = creationNoteState.template.criteria.size == 1
                val noCandidatesComparison = creationNoteState.alternatives.size == 1
                if (noTemplateCriteriaComparison && noCandidatesComparison) {
                    return@animatedComposable
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
                    creationNoteState = creationNoteState,
                    relationCardActions = cardActions,
                    relationScale = relationScale
                )
            }
            animatedComposable(
                route = "${MaiScreen.Result.name}/{note_id}",
                arguments = listOf(navArgument("note_id") { type = NavType.LongType })
            ) { backStackEntry ->
                val noteId = backStackEntry.arguments?.getLong("note_id")!!
                val note = uiState.notes.find { note -> noteId == note.id }
                if (note != null) {
                    ResultScreen(
                        note = note,
                        finalWeights = maiState.finalWeights,
                        crOfCriteriaMatrix = maiState.crOfCriteriaMatrix,
                        crsOfEachAlternativesMatrices = maiState.crsOfEachAlternativesMatrices
                    )
                } else {
                    navController.popBackStack(route = MaiScreen.Notes.name, inclusive = false)
                }
            }
        }
    }
}

private fun NavGraphBuilder.animatedComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = {
        slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) { it / 2 } + fadeIn(
            animationSpec = tween(easing = EaseInSine)
        )
    },
    exitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = {
        fadeOut(
            animationSpec = tween(easing = EaseOutSine)
        )
    },
    popEnterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        enterTransition,
    popExitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content
    )
}