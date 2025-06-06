package com.tunagold.oceantunes.utils

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tunagold.oceantunes.R
import kotlinx.coroutines.tasks.await

class GoogleAuthHelper(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)

    suspend fun beginSignIn(): BeginSignInResult {
        return oneTapClient.beginSignIn(buildSignInRequest()).await()
    }

    suspend fun handleSignInResult(data: Intent?): AuthResult {
        return try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data!!)
            val googleIdToken = credential.googleIdToken
                ?: throw Exception("Google ID token non disponibile")

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            auth.signInWithCredential(firebaseCredential).await()
            AuthResult.Success
        } catch (e: ApiException) {
            AuthResult.Failure(e)
        } catch (e: Exception) {
            AuthResult.Failure(e)
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    sealed class AuthResult {
        object Success : AuthResult()
        data class Failure(val exception: Exception) : AuthResult()
    }
}