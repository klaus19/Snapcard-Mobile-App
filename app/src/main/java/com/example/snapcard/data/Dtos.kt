package com.example.snapcard.data

data class FlashcardDto(
    val question: String,
    val answer: String,
    val hint: String,
    val topic: String
)

data class FlashcardSetDto(
    val title: String,
    val subject: String,
    val total_cards: Int,
    val flashcards: List<FlashcardDto>
)