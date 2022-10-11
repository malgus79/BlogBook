package com.blogappdemo.domain.auth

import com.blogappdemo.data.remote.auth.LoginDataSource
import com.google.firebase.auth.FirebaseUser

class LoginRepoImpl(private val dataSource: LoginDataSource) : LoginRepo {

    override suspend fun signIn(email: String, password: String): FirebaseUser? = dataSource.signIn(email, password)
}