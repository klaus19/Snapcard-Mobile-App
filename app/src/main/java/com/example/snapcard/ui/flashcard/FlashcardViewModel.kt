package com.example.snapcard.ui.flashcard

import com.example.snapcard.model.FlashcardSet
import com.example.snapcard.network.RetrofitClient

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

sealed class FlashcardUiState {
    object Idle : FlashcardUiState()
    object Loading : FlashcardUiState()
    data class Success(val flashcardSet: FlashcardSet) : FlashcardUiState()
    data class Error(val message: String) : FlashcardUiState()
}

class FlashcardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<FlashcardUiState>(FlashcardUiState.Idle)
    val uiState: StateFlow<FlashcardUiState> = _uiState

    fun generateFlashcards(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _uiState.value = FlashcardUiState.Loading
            try {
                // Read image bytes from URI
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val imageBytes = inputStream?.readBytes()
                    ?: throw Exception("Could not read image")
                inputStream.close()

                // Build multipart request
                val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("image", "photo.jpg", requestBody)

                // Call API
                val result = RetrofitClient.api.generateFlashcards(part)
                _uiState.value = FlashcardUiState.Success(result)

            } catch (e: Exception) {
                _uiState.value = FlashcardUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun reset() {
        _uiState.value = FlashcardUiState.Idle
    }
}