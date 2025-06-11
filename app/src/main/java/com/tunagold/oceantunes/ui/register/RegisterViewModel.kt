package com.tunagold.oceantunes.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth // Importa FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.tunagold.oceantunes.repository.user.IUserRepository
import com.tunagold.oceantunes.utils.Result

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: IUserRepository,
    private val firebaseAuth: FirebaseAuth // INIETTA FIREBASEAUTH QUI
) : ViewModel() {

    private val _signUpResult = MutableLiveData<Result<String>>()
    val signUpResult: LiveData<Result<String>> = _signUpResult

    fun signUp(email: String, password: String, username: String) {
        authRepository.signUp(email, password, username).observeForever {
            _signUpResult.value = it
        }
    }

    // Verifica se l'utente è già loggato
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}