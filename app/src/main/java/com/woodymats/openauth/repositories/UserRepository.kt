package com.woodymats.openauth.repositories

import android.content.SharedPreferences
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.network.RetrofitClient
import com.woodymats.openauth.utils.IS_USER_LOGGED_IN
import com.woodymats.openauth.utils.USER_ID
import com.woodymats.openauth.utils.USER_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository(private val database: AppDatabase? = null) {

    suspend fun registerUser(signUpEntity: SignUpEntity) {
        RetrofitClient.apiInterface.createAccount(signUpEntity)
    }

    suspend fun registerUserWithProfileImage(user: SignUpEntity) {

        val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), user.file!!)
        val file = MultipartBody.Part.createFormData("file", user.file!!.path, fileRequestBody) //createFormData("file", user.file!!.name, fileRequestBody)
        val firstName = RequestBody.create(MultipartBody.FORM, user.firstName)
        val lastName = RequestBody.create(MultipartBody.FORM, user.lastName)
        val email = RequestBody.create(MultipartBody.FORM, user.email)
        val password = RequestBody.create(MultipartBody.FORM, user.password)
        val dateOfBirth = RequestBody.create(MultipartBody.FORM, user.dateOfBirth.toString())
        val canCreateCourses = RequestBody.create(MultipartBody.FORM, user.canCreateCourses.toString())

        val temp = RetrofitClient.apiInterface.createAccountWithProfileImage(file, firstName, lastName, email, password, dateOfBirth, canCreateCourses)
    }

    suspend fun loginUser(loginEntity: LoginEntity, preferences: SharedPreferences) {
        val userFromNetwork = RetrofitClient.apiInterface.loginUser(loginEntity)
        database!!.userDAO.insertUser(userFromNetwork)
        preferences.edit().putBoolean(IS_USER_LOGGED_IN, true)
            .putString(USER_TOKEN, "Bearer " + userFromNetwork.token).putLong(USER_ID, userFromNetwork.id)
            .apply()
    }

    suspend fun logoutUser(database: AppDatabase, token: String, preferences: SharedPreferences) {
        RetrofitClient.apiInterface.logout(token)
        withContext(Dispatchers.IO) {
            database.clearAllTables()
            preferences.edit()
                .putBoolean(IS_USER_LOGGED_IN, false)
                .putString(USER_TOKEN, "")
                .putLong(USER_ID, -1L)
                .apply()
        }
    }
}