package com.example.snapcard.model

data class Flashcard(
    val id: Long = 0,
    val setId: Long = 0,
    val question: String,
    val answer: String,
    val hint: String,
    val topic: String,

    // SM-2 fields
    val easeFactor: Float = 2.5f,
    val interval: Int = 0,
    val repetitions: Int = 0,
    val nextReview: Long = System.currentTimeMillis(),
    val lastReviewed: Long? = null
)

data class FlashcardSet(
    val id: Long = 0,
    val title: String,
    val subject: String,
    val totalCards: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val flashcards: List<Flashcard> = emptyList()
)

data class StudySession(
    val id: Long = 0,
    val setId: Long,
    val date: Long = System.currentTimeMillis(),
    val cardsStudied: Int,
    val correctCount: Int,
    val timeSpentMs: Long
) {
    val accuracy: Float
        get() = if (cardsStudied > 0) correctCount.toFloat() / cardsStudied else 0f
}