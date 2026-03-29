package com.example.snapcard.ui.profile

import androidx.lifecycle.ViewModel
import com.example.snapcard.data.datastore.UserPreferences
import com.example.snapcard.data.repository.FlashcardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    repository: FlashcardRepository,
    userPreferences: UserPreferences
) : ViewModel() {

    val totalSets: StateFlow<Int> = repository.getTotalSetCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalCardsStudied: StateFlow<Int> = repository.getTotalCardsStudied()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val studyStreak: StateFlow<Int> = userPreferences.studyStreak
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}