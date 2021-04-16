package com.woodymats.openauth.repositories

import android.content.SharedPreferences
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.network.RetrofitClient
import com.woodymats.openauth.utils.IS_USER_LOGGED_IN
import com.woodymats.openauth.utils.USER_TOKEN

class LoginRepository(private val database: AppDatabase) {

    suspend fun loginUser(loginEntity: LoginEntity, preferences: SharedPreferences) {
        val userFromNetwork = RetrofitClient.apiInterface.loginUser(loginEntity)
        database.userDAO.insertUser(userFromNetwork)
        preferences.edit().putBoolean(IS_USER_LOGGED_IN, true).putString(USER_TOKEN, userFromNetwork.token).apply()
    }

}