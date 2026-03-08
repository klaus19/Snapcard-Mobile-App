package com.example.snapcard.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.snapcard.ui.camera.CameraScreen
import com.example.snapcard.ui.flashcard.FlashcardScreen
import com.example.snapcard.ui.flashcard.FlashcardViewModel
import com.example.snapcard.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Flashcards : Screen("flashcards")
}

@Composable
fun NavGraph(navController: NavHostController) {
    val flashcardViewModel: FlashcardViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Camera.route) {
            CameraScreen(
                navController = navController,
                viewModel = flashcardViewModel
            )
        }
        composable(Screen.Flashcards.route) {
            FlashcardScreen(
                navController = navController,
                viewModel = flashcardViewModel
            )
        }
    }
}