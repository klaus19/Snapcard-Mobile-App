package com.example.snapcard.auth

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.example.snapcard.R

@Singleton
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    private val credentialManager = CredentialManager.create(context)

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isSignedIn: Boolean
        get() = firebaseAuth.currentUser != null

    suspend fun signInWithGoogle(activityContext: Context): FirebaseUser {
        // 1. Build Google sign-in option
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .build()

        // 2. Create credential request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // 3. Launch Credential Manager (needs Activity context)
        val result = credentialManager.getCredential(activityContext, request)

        // 4. Extract Google ID token
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(
            result.credential.data
        )
        val idToken = googleIdTokenCredential.idToken

        // 5. Sign in to Firebase with the token
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()

        return authResult.user ?: throw Exception("Firebase sign-in failed")
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {
            // Credential state clear is best-effort
        }
    }
}