package com.tunagold.oceantunes.source.user

import com.tunagold.oceantunes.model.User

abstract class BaseUserDataRemoteDataSource {
    abstract fun saveUser(user: User)

}