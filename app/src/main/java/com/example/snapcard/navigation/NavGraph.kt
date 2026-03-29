package com.example.snapcard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.snapcard.auth.AuthUiState
import com.example.snapcard.auth.AuthViewModel
import com.example.snapcard.login.LoginScreen
import com.example.snapcard.ui.camera.CameraScreen
import com.example.snapcard.ui.flashcard.FlashcardScreen
import com.example.snapcard.ui.flashcard.FlashcardViewModel
import com.example.snapcard.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Camera : Screen("camera")
    object Flashcards : Screen("flashcards")
}

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val flashcardViewModel: FlashcardViewModel = hiltViewModel()
    val authState by authViewModel.uiState.collectAsState()

    val startDestination = when (authState) {
        is AuthUiState.SignedIn -> Screen.Home.route
        else -> Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(authViewModel = authViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(
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
        composable(Screen.Flashcards.route) {
            FlashcardScreen(
                navController = navController,
                viewModel = flashcardViewModel
            )
        }
    }
}