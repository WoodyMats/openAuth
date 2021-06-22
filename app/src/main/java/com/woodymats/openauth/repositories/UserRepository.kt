package com.woodymats.openauth.repositories

import android.content.SharedPreferences
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.models.local.UserEntity
import com.woodymats.openauth.network.RetrofitClient
import com.woodymats.openauth.utils.IS_USER_LOGGED_IN
import com.woodymats.openauth.utils.USER_ID
import com.woodymats.openauth.utils.USER_TOKEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class UserRepository(private val database: AppDatabase? = null) {

    suspend fun registerUser(signUpEntity: SignUpEntity) {
        RetrofitClient.apiInterface.createAccount(signUpEntity)
    }

    suspend fun sendDeviceToken(token: String, deviceToken: String) {
        RetrofitClient.apiInterface.sendDeviceId(token, deviceToken)
    }

    suspend fun registerUserWithProfileImage(user: SignUpEntity) {

        val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), user.file!!)
        val file = MultipartBody.Part.createFormData(
            "file",
            user.file!!.path,
            fileRequestBody
        ) // createFormData("file", user.file!!.name, fileRequestBody)
        val firstName = RequestBody.create(MultipartBody.FORM, user.firstName)
        val lastName = RequestBody.create(MultipartBody.FORM, user.lastName)
        val email = RequestBody.create(MultipartBody.FORM, user.email)
        val password = RequestBody.create(MultipartBody.FORM, user.password)
        val dateOfBirth = RequestBody.create(MultipartBody.FORM, user.dateOfBirth.toString())
        val canCreateCourses =
            RequestBody.create(MultipartBody.FORM, user.canCreateCourses.toString())

        RetrofitClient.apiInterface.createAccountWithProfileImage(
            file,
            firstName,
            lastName,
            email,
            password,
            dateOfBirth,
            canCreateCourses
        )
    }

    suspend fun loginUser(loginEntity: LoginEntity, preferences: SharedPreferences) {
        val userFromNetwork = RetrofitClient.apiInterface.loginUser(loginEntity)
        database!!.userDAO.insertUser(userFromNetwork)
        preferences.edit().putBoolean(IS_USER_LOGGED_IN, true)
            .putString(USER_TOKEN, "Bearer " + userFromNetwork.token)
            .putLong(USER_ID, userFromNetwork.id)
            .apply()
    }

    suspend fun logoutUser(database: AppDatabase, token: String, preferences: SharedPreferences) {
        RetrofitClient.apiInterface.logout(token, logoutFromAndroid = true)
        withContext(Dispatchers.IO) {
            database.clearAllTables()
            preferences.edit()
                .putBoolean(IS_USER_LOGGED_IN, false)
                .putString(USER_TOKEN, "")
                .putLong(USER_ID, -1L)
                .apply()
        }
    }

    suspend fun getUser(): UserEntity = database!!.userDAO.getUser()

    suspend fun updateUserWithFile(
        token: String,
        firstName: String,
        lastName: String,
        dateOfBirth: Long,
        profileImageFile: File
    ): UserEntity {
        val fileRequestBody = RequestBody.create(MediaType.parse("image/*"), profileImageFile)
        val file = MultipartBody.Part.createFormData("file", profileImageFile.path, fileRequestBody)
        val firstNameRb = RequestBody.create(MultipartBody.FORM, firstName)
        val lastNameRb = RequestBody.create(MultipartBody.FORM, lastName)
        val dateOfBirthRb = RequestBody.create(MultipartBody.FORM, dateOfBirth.toString())

        val user = RetrofitClient.apiInterface.updateUserWithFile(
            token,
            file,
            firstNameRb,
            lastNameRb,
            dateOfBirthRb
        )
        val a = true
        if (user != null) {
            database?.userDAO?.updateUser(user)
        }
        return database?.userDAO!!.getUser()
    }

    suspend fun updateUserWithoutFile(
        token: String,
        firstName: String,
        lastName: String,
        dateOfBirth: Long
    ): UserEntity {
        val user = RetrofitClient.apiInterface.updateUserWithoutFile(
            token,
            firstName,
            lastName,
            dateOfBirth
        )
        if (user != null) {
            database?.userDAO?.updateUser(user)
        }
        return database?.userDAO!!.getUser()
    }
}
