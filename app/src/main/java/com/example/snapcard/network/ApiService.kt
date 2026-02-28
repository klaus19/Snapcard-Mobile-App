package com.example.snapcard.network


import com.example.snapcard.model.FlashcardSet
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("generate-flashcards")
    suspend fun generateFlashcards(
        @Part image: MultipartBody.Part
    ): FlashcardSet
}