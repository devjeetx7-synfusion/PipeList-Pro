package com.synfusion.pipelistpro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.ui.components.ModernBottomNavigation
import com.synfusion.pipelistpro.ui.screens.*
import com.synfusion.pipelistpro.ui.theme.PipeListProTheme
import com.synfusion.pipelistpro.viewmodel.ProjectViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ProjectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeListProTheme {
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: ProjectViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "splash") {
                ModernBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavigation(navController, viewModel)
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: ProjectViewModel) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("home") {
            HomeScreen(viewModel, navController)
        }
        composable("material") {
            MaterialScreen(viewModel, navController)
        }
        composable("project_list") {
            ProjectListScreen(viewModel, navController)
        }
        composable("settings") {
            SettingsScreen(viewModel, navController)
        }
        composable("statistics") {
            StatisticsScreen(viewModel, navController)
        }
    }
}
