package com.woodymats.openauth.repositories

import androidx.lifecycle.asLiveData
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.databases.UserDAO
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.User
import com.woodymats.openauth.network.ApiCallsInterface
import com.woodymats.openauth.utils.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class LoginRepository(
    val api: ApiCallsInterface,
    val userDAO: UserDAO
) {

    suspend fun loginUser(loginEntity: LoginEntity): DataState<User> {
        return try {
            val userFromNetwork = api.loginUser(loginEntity)
            userDAO.insertUser(userFromNetwork)
            val cachedUser = userDAO.getUser()
            DataState.Success(cachedUser)
        } catch (e: Exception) {
            DataState.Error(e)
        }
    }
}