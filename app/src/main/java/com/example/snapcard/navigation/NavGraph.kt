package com.example.snapcard.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.snapcard.auth.AuthUiState
import com.example.snapcard.auth.AuthViewModel
import com.example.snapcard.login.LoginScreen
import com.example.snapcard.ui.camera.CameraScreen
import com.example.snapcard.ui.flashcard.FlashcardScreen
import com.example.snapcard.ui.flashcard.FlashcardViewModel
import com.example.snapcard.ui.home.HomeScreen
import com.example.snapcard.ui.library.LibraryScreen
import com.example.snapcard.ui.profile.ProfileScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Library : Screen("library")
    object Profile : Screen("profile")
    object Camera : Screen("camera")
    object Flashcards : Screen("flashcards/{setId}") {
        fun createRoute(setId: Long) = "flashcards/$setId"
    }
    object NewFlashcards : Screen("flashcards_new")
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: String
)

val bottomNavItems = listOf(
    BottomNavItem("home", "Home", "🏠"),
    BottomNavItem("library", "Library", "📚"),
    BottomNavItem("profile", "Profile", "👤")
)

// Routes that should show the bottom bar
private val bottomBarRoutes = setOf("home", "library", "profile")

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val flashcardViewModel: FlashcardViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    val startDestination = when (authState) {
        is AuthUiState.SignedIn -> Screen.Home.route
        else -> Screen.Login.route
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(
                bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
            )
        ) {
            composable(Screen.Login.route) {
                LoginScreen(authViewModel = authViewModel)
            }
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.Library.route) {
                LibraryScreen(navController = navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
            composable(Screen.Camera.route) {
                CameraScreen(
                    navController = navController,
                    viewModel = flashcardViewModel
                )
            }
            // New flashcards (just generated, no setId)
            composable(Screen.NewFlashcards.route) {
                FlashcardScreen(
                    navController = navController,
                    viewModel = flashcardViewModel
                )
            }
            // View saved set by ID
            composable(
                route = Screen.Flashcards.route,
                arguments = listOf(navArgument("setId") { type = NavType.LongType })
            ) { backStackEntry ->
                val setId = backStackEntry.arguments?.getLong("setId") ?: return@composable
                LaunchedEffect(setId) {
                    flashcardViewModel.loadSavedSet(setId)
                }
                FlashcardScreen(
                    navController = navController,
                    viewModel = flashcardViewModel
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF6366F1),
        tonalElevation = 0.dp,
        modifier = Modifier
            .navigationBarsPadding()
            .height(64.dp)
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Text(
                        text = item.icon,
                        fontSize = if (selected) 22.sp else 20.sp
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (selected) Color(0xFF6366F1) else Color(0xFF9CA3AF)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFEEF2FF)
                )
            )
        }
    }
}