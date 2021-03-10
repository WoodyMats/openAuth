package com.woodymats.openauth.network

import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.User
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiCallsInterface {

    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginUser(@Body entity: LoginEntity): User
}