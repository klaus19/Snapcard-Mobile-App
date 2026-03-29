package com.example.snapcard.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthUiState {
    object Loading : AuthUiState()
    object SignedOut : AuthUiState()
    data class SignedIn(val user: FirebaseUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = authRepository.currentUser
        _uiState.value = if (user != null) {
            AuthUiState.SignedIn(user)
        } else {
            AuthUiState.SignedOut
        }
    }

    fun signInWithGoogle(activityContext: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val user = authRepository.signInWithGoogle(activityContext)
                _uiState.value = AuthUiState.SignedIn(user)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Sign-in failed"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState.SignedOut
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.SignedOut
    }
}