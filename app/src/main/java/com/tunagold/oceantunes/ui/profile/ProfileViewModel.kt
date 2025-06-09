package com.tunagold.oceantunes.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunagold.oceantunes.model.User
import com.tunagold.oceantunes.repository.user.IUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: IUserRepository
) : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _updateUsernameResult = MutableLiveData<Result<String>>()
    val updateUsernameResult: LiveData<Result<String>> = _updateUsernameResult

    private val _signOutResult = MutableLiveData<Result<String>>()
    val signOutResult: LiveData<Result<String>> = _signOutResult

    fun fetchCurrentUserDetails() {
        viewModelScope.launch {
            userRepository.getCurrentUserDetails().observeForever { result ->
                result.onSuccess { user ->
                    _currentUser.value = user
                }.onFailure {}
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            userRepository.updateUsername(newUsername).observeForever { result ->
                _updateUsernameResult.value = result
                // If update is successful, also refresh the current user details
                if (result.isSuccess) {
                    fetchCurrentUserDetails()
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut().observeForever { result ->
                _signOutResult.value = result
            }
        }
    }
}
