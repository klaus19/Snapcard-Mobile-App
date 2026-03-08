package com.example.snapcard.data

import com.example.snapcard.db.FlashcardEntity
import com.example.snapcard.db.FlashcardSetEntity
import com.example.snapcard.db.StudySessionEntity
import com.example.snapcard.model.Flashcard
import com.example.snapcard.model.FlashcardSet
import com.example.snapcard.model.StudySession

// ═══════════════════════════════
// Entity → Domain
// ═══════════════════════════════

fun FlashcardSetEntity.toDomain(cards: List<Flashcard> = emptyList()) = FlashcardSet(
    id = id,
    title = title,
    subject = subject,
    totalCards = totalCards,
    createdAt = createdAt,
    imageUri = imageUri,
    flashcards = cards
)

fun FlashcardEntity.toDomain() = Flashcard(
    id = id,
    setId = setId,
    question = question,
    answer = answer,
    hint = hint,
    topic = topic,
    easeFactor = easeFactor,
    interval = interval,
    repetitions = repetitions,
    nextReview = nextReview,
    lastReviewed = lastReviewed
)

fun StudySessionEntity.toDomain() = StudySession(
    id = id,
    setId = setId,
    date = date,
    cardsStudied = cardsStudied,
    correctCount = correctCount,
    timeSpentMs = timeSpentMs
)

// ═══════════════════════════════
// Domain → Entity
// ═══════════════════════════════

fun FlashcardSet.toEntity() = FlashcardSetEntity(
    id = id,
    title = title,
    subject = subject,
    totalCards = totalCards,
    createdAt = createdAt,
    imageUri = imageUri
)

fun Flashcard.toEntity() = FlashcardEntity(
    id = id,
    setId = setId,
    question = question,
    answer = answer,
    hint = hint,
    topic = topic,
    easeFactor = easeFactor,
    interval = interval,
    repetitions = repetitions,
    nextReview = nextReview,
    lastReviewed = lastReviewed
)

fun StudySession.toEntity() = StudySessionEntity(
    id = id,
    setId = setId,
    date = date,
    cardsStudied = cardsStudied,
    correctCount = correctCount,
    timeSpentMs = timeSpentMs
)

// ═══════════════════════════════
// Network DTO → Domain
// ═══════════════════════════════

fun FlashcardDto.toDomain() = Flashcard(
    question = question,
    answer = answer,
    hint = hint,
    topic = topic
)

fun FlashcardSetDto.toDomain() = FlashcardSet(
    title = title,
    subject = subject,
    totalCards = total_cards,
    flashcards = flashcards.map { it.toDomain() }
)