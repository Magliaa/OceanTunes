package com.tunagold.oceantunes.ui.login

import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunagold.oceantunes.repository.user.IUserRepository
import com.tunagold.oceantunes.utils.GoogleAuthHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.tunagold.oceantunes.utils.Result

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: IUserRepository,
    private val googleAuthHelper: GoogleAuthHelper,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _signInResult = MutableLiveData<Result<String>>()
    val signInResult: LiveData<Result<String>> = _signInResult

    private val _googleSignInResult = MutableLiveData<Result<String>>()
    val googleSignInResult: LiveData<Result<String>> = _googleSignInResult

    private val _googleSignInIntentSenderRequest = MutableLiveData<PendingIntent>()
    val googleSignInIntentSenderRequest: LiveData<PendingIntent> = _googleSignInIntentSenderRequest

    fun signIn(email: String, password: String) {
        _signInResult.value = Result.Loading
        viewModelScope.launch {
            userRepository.signIn(email, password).observeForever { result ->
                _signInResult.value = result
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun initiateGoogleSignIn() {
        _googleSignInResult.value = Result.Loading
        viewModelScope.launch {
            try {
                val signInResult = googleAuthHelper.beginSignIn()
                _googleSignInIntentSenderRequest.value = signInResult.pendingIntent
            } catch (e: Exception) {
                _googleSignInResult.value = Result.Error(e)
            }
        }
    }

    fun handleGoogleSignInIntentResult(data: Intent) {
        _googleSignInResult.value = Result.Loading
        viewModelScope.launch {
            when (val authResult = googleAuthHelper.handleSignInResult(data)) {
                is GoogleAuthHelper.AuthResult.Success -> {
                    _googleSignInResult.value = Result.Success("Autenticazione Google/Firebase completata")
                }
                is GoogleAuthHelper.AuthResult.Failure -> {
                    _googleSignInResult.value = Result.Error(authResult.exception)
                }
            }
        }
    }
}