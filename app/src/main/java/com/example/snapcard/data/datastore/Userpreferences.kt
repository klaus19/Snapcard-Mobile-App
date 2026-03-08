package com.example.snapcard.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val DAILY_SCAN_COUNT = intPreferencesKey("daily_scan_count")
        val LAST_SCAN_DATE = longPreferencesKey("last_scan_date")
        val IS_PRO_USER = booleanPreferencesKey("is_pro_user")
        val STUDY_STREAK = intPreferencesKey("study_streak")
        val LAST_STUDY_DATE = longPreferencesKey("last_study_date")
    }

    val isOnboardingComplete: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETE] ?: false
    }

    suspend fun setOnboardingComplete() {
        dataStore.edit { prefs -> prefs[Keys.ONBOARDING_COMPLETE] = true }
    }

    val dailyScanCount: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.DAILY_SCAN_COUNT] ?: 0
    }

    suspend fun incrementScanCount(): Int {
        var newCount = 0
        dataStore.edit { prefs ->
            val today = System.currentTimeMillis() / 86_400_000L
            val lastScanDay = (prefs[Keys.LAST_SCAN_DATE] ?: 0L) / 86_400_000L

            newCount = if (today > lastScanDay) {
                1
            } else {
                (prefs[Keys.DAILY_SCAN_COUNT] ?: 0) + 1
            }

            prefs[Keys.DAILY_SCAN_COUNT] = newCount
            prefs[Keys.LAST_SCAN_DATE] = System.currentTimeMillis()
        }
        return newCount
    }

    val isProUser: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.IS_PRO_USER] ?: false
    }

    suspend fun setProUser(isPro: Boolean) {
        dataStore.edit { prefs -> prefs[Keys.IS_PRO_USER] = isPro }
    }

    val studyStreak: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.STUDY_STREAK] ?: 0
    }

    suspend fun updateStudyStreak() {
        dataStore.edit { prefs ->
            val today = System.currentTimeMillis() / 86_400_000L
            val lastStudyDay = (prefs[Keys.LAST_STUDY_DATE] ?: 0L) / 86_400_000L
            val currentStreak = prefs[Keys.STUDY_STREAK] ?: 0

            when {
                today == lastStudyDay -> { /* already studied today */ }
                today - lastStudyDay == 1L -> {
                    prefs[Keys.STUDY_STREAK] = currentStreak + 1
                    prefs[Keys.LAST_STUDY_DATE] = System.currentTimeMillis()
                }
                else -> {
                    prefs[Keys.STUDY_STREAK] = 1
                    prefs[Keys.LAST_STUDY_DATE] = System.currentTimeMillis()
                }
            }
        }
    }

    companion object {
        const val FREE_DAILY_SCAN_LIMIT = 5
    }
}