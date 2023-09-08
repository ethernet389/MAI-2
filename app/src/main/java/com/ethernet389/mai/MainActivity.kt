package com.ethernet389.mai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
    Scaffold(
        topBar = {},
        bottomBar = {
            NavigationBottomBar(
                items = listOf("this" to Icons.Filled.Add),
                selectedItem = 0,
                onNavButtonClick = {}
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "strb",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = "strb") {}
        }
    }
}

@Composable
fun NavigationBottomBar(
    modifier: Modifier = Modifier,
    items: List<Pair<String, ImageVector>>,
    selectedItem: Int,
    onNavButtonClick: () -> Unit
) {
    NavigationBar(
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = onNavButtonClick,
                icon = { Icon(imageVector = item.second, contentDescription = item.first) },
                label = { Text(item.first) }
            )
        }
    }
}