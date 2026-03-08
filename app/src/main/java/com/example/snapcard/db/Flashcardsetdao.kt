package com.example.snapcard.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// ═══════════════════════════════
// FlashcardSetDao
// ═══════════════════════════════

@Dao
interface FlashcardSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: FlashcardSetEntity): Long

    @Query("SELECT * FROM flashcard_sets ORDER BY createdAt DESC")
    fun getAllSets(): Flow<List<FlashcardSetEntity>>

    @Query("SELECT * FROM flashcard_sets WHERE id = :setId")
    suspend fun getSetById(setId: Long): FlashcardSetEntity?

    @Query("SELECT * FROM flashcard_sets WHERE title LIKE '%' || :query || '%' OR subject LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchSets(query: String): Flow<List<FlashcardSetEntity>>

    @Query("SELECT COUNT(*) FROM flashcard_sets")
    fun getTotalSetCount(): Flow<Int>

    @Delete
    suspend fun deleteSet(set: FlashcardSetEntity)

    @Query("DELETE FROM flashcard_sets WHERE id = :setId")
    suspend fun deleteSetById(setId: Long)
}

// ═══════════════════════════════
// FlashcardDao
// ═══════════════════════════════

@Dao
interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCards(cards: List<FlashcardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: FlashcardEntity): Long

    @Query("SELECT * FROM flashcards WHERE setId = :setId ORDER BY id ASC")
    fun getCardsForSet(setId: Long): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE setId = :setId ORDER BY id ASC")
    suspend fun getCardsForSetOnce(setId: Long): List<FlashcardEntity>

    @Query("SELECT * FROM flashcards WHERE setId = :setId AND nextReview <= :now ORDER BY nextReview ASC")
    suspend fun getDueCards(setId: Long, now: Long = System.currentTimeMillis()): List<FlashcardEntity>

    @Query("SELECT * FROM flashcards WHERE nextReview <= :now ORDER BY nextReview ASC")
    suspend fun getAllDueCards(now: Long = System.currentTimeMillis()): List<FlashcardEntity>

    @Query("SELECT COUNT(*) FROM flashcards WHERE nextReview <= :now")
    fun getDueCardCount(now: Long = System.currentTimeMillis()): Flow<Int>

    @Update
    suspend fun updateCard(card: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE setId = :setId")
    suspend fun deleteCardsForSet(setId: Long)
}

// ═══════════════════════════════
// StudySessionDao
// ═══════════════════════════════

@Dao
interface StudySessionDao {

    @Insert
    suspend fun insertSession(session: StudySessionEntity)

    @Query("SELECT * FROM study_sessions WHERE setId = :setId ORDER BY date DESC")
    fun getSessionsForSet(setId: Long): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions ORDER BY date DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 10): Flow<List<StudySessionEntity>>

    @Query("SELECT COALESCE(SUM(cardsStudied), 0) FROM study_sessions")
    fun getTotalCardsStudied(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT date / 86400000) FROM study_sessions WHERE date >= :sinceTimestamp")
    suspend fun getStudyDaysCount(sinceTimestamp: Long): Int
}