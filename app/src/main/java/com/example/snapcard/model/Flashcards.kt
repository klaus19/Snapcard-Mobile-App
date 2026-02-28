package com.example.snapcard.model

data class Flashcard(
    val question: String,
    val answer: String,
    val hint: String,
    val topic: String
)

data class FlashcardSet(
    val title: String,
    val subject: String,
    val total_cards: Int,
    val flashcards: List<Flashcard>
)