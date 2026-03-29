package com.example.snapcard.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.snapcard.db.FlashcardDao
import com.example.snapcard.db.FlashcardSetDao
import com.example.snapcard.db.SnapCardDatabase
import com.example.snapcard.db.StudySessionDao
import com.example.snapcard.network.ApiService
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "snapcard_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ═══════════════════════════════
    // Room Database
    // ═══════════════════════════════

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SnapCardDatabase {
        return Room.databaseBuilder(
            context,
            SnapCardDatabase::class.java,
            "snapcard_db"
        ).build()
    }

    @Provides fun provideFlashcardSetDao(db: SnapCardDatabase): FlashcardSetDao = db.flashcardSetDao()
    @Provides fun provideFlashcardDao(db: SnapCardDatabase): FlashcardDao = db.flashcardDao()
    @Provides fun provideStudySessionDao(db: SnapCardDatabase): StudySessionDao = db.studySessionDao()

    // ═══════════════════════════════
    // Retrofit
    // ═══════════════════════════════

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            //.baseUrl("http://192.168.31.88:8000/")
            //.baseUrl("http://192.168.0.100:8000/")
            .baseUrl("http://192.168.31.88:8000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // ═══════════════════════════════
    // DataStore
    // ═══════════════════════════════

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    // Add inside the AppModule object:
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}