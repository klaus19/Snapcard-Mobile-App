package com.example.snapcard.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FlashcardSetEntity::class,
        FlashcardEntity::class,
        StudySessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SnapCardDatabase : RoomDatabase() {
    abstract fun flashcardSetDao(): FlashcardSetDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun studySessionDao(): StudySessionDao
}