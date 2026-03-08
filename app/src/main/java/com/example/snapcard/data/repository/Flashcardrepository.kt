package com.example.snapcard.data.repository

import android.content.Context
import android.net.Uri
import com.example.snapcard.data.datastore.UserPreferences
import com.example.snapcard.db.FlashcardDao
import com.example.snapcard.db.FlashcardSetDao
import com.example.snapcard.db.StudySessionDao
import com.example.snapcard.data.toDomain
import com.example.snapcard.data.toEntity
import com.example.snapcard.network.ApiService
import com.example.snapcard.model.Flashcard
import com.example.snapcard.model.FlashcardSet
import com.example.snapcard.model.StudySession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepository @Inject constructor(
    private val apiService: ApiService,
    private val flashcardSetDao: FlashcardSetDao,
    private val flashcardDao: FlashcardDao,
    private val studySessionDao: StudySessionDao,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context
) {

    // ═══════════════════════════════════════
    // Remote: Generate flashcards from image
    // ═══════════════════════════════════════

    suspend fun generateAndSaveFlashcards(imageUri: Uri): FlashcardSet {
        // 1. Read image bytes
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val imageBytes = inputStream?.readBytes()
            ?: throw Exception("Could not read image")
        inputStream.close()

        // 2. Call backend API
        val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", "photo.jpg", requestBody)
        val response = apiService.generateFlashcards(part)

        // 3. Convert to domain model
        val flashcardSet = response.toDomain()

        // 4. Save set to Room → get generated ID
        val setEntity = flashcardSet.toEntity().copy(imageUri = imageUri.toString())
        val setId = flashcardSetDao.insertSet(setEntity)

        // 5. Save cards with the set ID
        val cardEntities = flashcardSet.flashcards.map { card ->
            card.toEntity().copy(setId = setId)
        }
        flashcardDao.insertCards(cardEntities)

        // 6. Increment scan count
        userPreferences.incrementScanCount()

        // 7. Return complete domain model with IDs
        val savedCards = flashcardDao.getCardsForSetOnce(setId).map { it.toDomain() }
        return flashcardSet.copy(id = setId, flashcards = savedCards)
    }

    // ═══════════════════════════════════════
    // Local: Read saved flashcard sets
    // ═══════════════════════════════════════

    fun getAllSets(): Flow<List<FlashcardSet>> {
        return flashcardSetDao.getAllSets().map { sets ->
            sets.map { it.toDomain() }
        }
    }

    suspend fun getSetWithCards(setId: Long): FlashcardSet? {
        val setEntity = flashcardSetDao.getSetById(setId) ?: return null
        val cards = flashcardDao.getCardsForSetOnce(setId).map { it.toDomain() }
        return setEntity.toDomain(cards)
    }

    fun searchSets(query: String): Flow<List<FlashcardSet>> {
        return flashcardSetDao.searchSets(query).map { sets ->
            sets.map { it.toDomain() }
        }
    }

    suspend fun deleteSet(setId: Long) {
        flashcardSetDao.deleteSetById(setId)
    }

    // ═══════════════════════════════════════
    // Spaced Repetition
    // ═══════════════════════════════════════

    suspend fun getDueCards(setId: Long): List<Flashcard> {
        return flashcardDao.getDueCards(setId).map { it.toDomain() }
    }

    suspend fun getAllDueCards(): List<Flashcard> {
        return flashcardDao.getAllDueCards().map { it.toDomain() }
    }

    fun getDueCardCount(): Flow<Int> {
        return flashcardDao.getDueCardCount()
    }

    suspend fun updateCardAfterReview(card: Flashcard) {
        flashcardDao.updateCard(card.toEntity())
    }

    // ═══════════════════════════════════════
    // Study Sessions
    // ═══════════════════════════════════════

    suspend fun saveStudySession(session: StudySession) {
        studySessionDao.insertSession(session.toEntity())
        userPreferences.updateStudyStreak()
    }

    fun getRecentSessions(limit: Int = 10): Flow<List<StudySession>> {
        return studySessionDao.getRecentSessions(limit).map { sessions ->
            sessions.map { it.toDomain() }
        }
    }

    fun getTotalCardsStudied(): Flow<Int> {
        return studySessionDao.getTotalCardsStudied()
    }

    fun getTotalSetCount(): Flow<Int> {
        return flashcardSetDao.getTotalSetCount()
    }
}
