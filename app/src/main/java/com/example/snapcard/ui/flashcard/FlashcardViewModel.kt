package com.example.snapcard.ui.flashcard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snapcard.data.repository.FlashcardRepository
import com.example.snapcard.model.FlashcardSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FlashcardUiState {
    object Idle : FlashcardUiState()
    object Loading : FlashcardUiState()
    data class Success(val flashcardSet: FlashcardSet) : FlashcardUiState()
    data class Error(val message: String) : FlashcardUiState()
}

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: FlashcardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FlashcardUiState>(FlashcardUiState.Idle)
    val uiState: StateFlow<FlashcardUiState> = _uiState

    fun generateFlashcards(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = FlashcardUiState.Loading
            try {
                val flashcardSet = repository.generateAndSaveFlashcards(imageUri)
                _uiState.value = FlashcardUiState.Success(flashcardSet)
            } catch (e: Exception) {
                _uiState.value = FlashcardUiState.Error(
                    e.message ?: "Something went wrong"
                )
            }
        }
    }

    fun loadSavedSet(setId: Long) {
        viewModelScope.launch {
            _uiState.value = FlashcardUiState.Loading
            try {
                val set = repository.getSetWithCards(setId)
                if (set != null) {
                    _uiState.value = FlashcardUiState.Success(set)
                } else {
                    _uiState.value = FlashcardUiState.Error("Set not found")
                }
            } catch (e: Exception) {
                _uiState.value = FlashcardUiState.Error(e.message ?: "Failed to load set")
            }
        }
    }

    fun reset() {
        _uiState.value = FlashcardUiState.Idle
    }
}