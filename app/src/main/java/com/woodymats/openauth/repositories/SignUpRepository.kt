package com.woodymats.openauth.repositories

import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.network.RetrofitClient

class SignUpRepository(private val database: AppDatabase) {

    suspend fun registerUser(signUpEntity: SignUpEntity) {
        val userFromNetwork = RetrofitClient.apiInterface.createAccount(signUpEntity)
        database.userDAO.insertUser(userFromNetwork)
    }
}