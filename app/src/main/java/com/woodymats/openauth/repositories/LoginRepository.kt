package com.woodymats.openauth.repositories

import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.network.RetrofitClient

class LoginRepository(private val database: AppDatabase) {

    suspend fun loginUser(loginEntity: LoginEntity) {
        val userFromNetwork = RetrofitClient.apiInterface.loginUser(loginEntity)
        database.userDAO.insertUser(userFromNetwork)
        database.userDAO.getUser(userFromNetwork.email)
    }
}