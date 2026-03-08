package com.example.snapcard.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════
// FlashcardSetEntity
// ═══════════════════════════════

@Entity(tableName = "flashcard_sets")
data class FlashcardSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val totalCards: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val imageUri: String? = null
)

// ═══════════════════════════════
// FlashcardEntity
// ═══════════════════════════════

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = FlashcardSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["setId"])]
)
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val setId: Long,
    val question: String,
    val answer: String,
    val hint: String,
    val topic: String,

    // SM-2 Spaced Repetition fields
    val easeFactor: Float = 2.5f,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val nextReview: Long = System.currentTimeMillis(),
    val lastReviewed: Long? = null
)

// ═══════════════════════════════
// StudySessionEntity
// ═══════════════════════════════

@Entity(
    tableName = "study_sessions",
    foreignKeys = [
        ForeignKey(
            entity = FlashcardSetEntity::class,
            parentColumns = ["id"],
            childColumns = ["setId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["setId"])]
)
data class StudySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val setId: Long,
    val date: Long = System.currentTimeMillis(),
    val cardsStudied: Int,
    val correctCount: Int,
    val timeSpentMs: Long
)