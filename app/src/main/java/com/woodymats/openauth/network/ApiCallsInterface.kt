package com.woodymats.openauth.network

import com.google.gson.JsonObject
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.models.User
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiCallsInterface {

    @Headers("Content-Type: application/json")
    @POST("checkLogin")
    suspend fun loginUser(@Body entity: LoginEntity): User

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun createAccount(@Body signUpEntity: SignUpEntity): JsonObject
}