package com.synfusion.pipelistpro.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.core.theme.PipeListProTheme
import com.synfusion.pipelistpro.core.utils.GlobalCrashHandler
import com.synfusion.pipelistpro.data.models.ThemeMode
import com.synfusion.pipelistpro.features.cart.ProjectListScreen
import com.synfusion.pipelistpro.features.cart.ProjectViewModel
import com.synfusion.pipelistpro.features.home.HomeScreen
import com.synfusion.pipelistpro.features.home.SplashScreen
import com.synfusion.pipelistpro.features.materials.MaterialScreen
import com.synfusion.pipelistpro.features.settings.SettingsScreen
import com.synfusion.pipelistpro.ui.components.ModernBottomNavigation

class MainActivity : ComponentActivity() {
    private val viewModel: ProjectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler(GlobalCrashHandler(applicationContext))
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            PipeListProTheme(darkTheme = darkTheme) {
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
    val shouldShowBottomBar = currentRoute in setOf("home", "settings")

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                ModernBottomNavigation(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onAddClick = {
                        viewModel.startNewProject()
                        navController.navigate("material")
                    }
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AppNavigation(navController, viewModel)
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, viewModel: ProjectViewModel) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("home") { HomeScreen(viewModel, navController) }
        composable("settings") { SettingsScreen(viewModel, navController) }
        composable("material") { MaterialScreen(viewModel, navController) }
        composable("project_list") { ProjectListScreen(viewModel, navController) }
    }
}
