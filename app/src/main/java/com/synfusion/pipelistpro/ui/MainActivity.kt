package com.synfusion.pipelistpro.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.ui.Alignment
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.synfusion.pipelistpro.ui.components.ModernBottomNavigation
import com.synfusion.pipelistpro.features.home.*
import com.synfusion.pipelistpro.features.cart.*
import com.synfusion.pipelistpro.features.materials.*
import com.synfusion.pipelistpro.features.settings.*
import com.synfusion.pipelistpro.core.theme.PipeListProTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: ProjectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.setDefaultUncaughtExceptionHandler(com.synfusion.pipelistpro.core.utils.GlobalCrashHandler(applicationContext))
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("pipelist_prefs", Context.MODE_PRIVATE)
            val isDarkMode by viewModel.isDarkMode.collectAsState(
                initial = prefs.getBoolean("dark_mode", false)
            )

            PipeListProTheme(darkTheme = isDarkMode) {
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

    Box(modifier = Modifier.fillMaxSize()) {
        val bottomBarRoutes = setOf("home", "settings")
        val shouldShowBottomBar = currentRoute in bottomBarRoutes

        // Main Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = if (shouldShowBottomBar) WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() else 0.dp
                )
        ) {
            AppNavigation(navController, viewModel)
        }

        // Truly Floating Bottom Navigation
        if (shouldShowBottomBar) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
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
                    },
                    onAddClick = {
                        viewModel.startNewProject()
                        navController.navigate("material")
                    }
                )
            }
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
            MainPager(viewModel, navController, 0)
        }
        composable("settings") {
            MainPager(viewModel, navController, 1)
        }
        composable("material") {
            MaterialScreen(viewModel, navController)
        }
        composable("project_list") {
            ProjectListScreen(viewModel, navController)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPager(viewModel: ProjectViewModel, navController: NavHostController, initialPage: Int) {
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { 2 })

    // Sync pager state with navigation
    LaunchedEffect(pagerState.currentPage) {
        val routes = listOf("home", "settings")
        val targetRoute = routes[pagerState.currentPage]
        if (navController.currentDestination?.route != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(navController.graph.startDestinationId) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        userScrollEnabled = true
    ) { page ->
        when (page) {
            0 -> HomeScreen(viewModel, navController)
            1 -> SettingsScreen(viewModel, navController)
        }
    }
}
