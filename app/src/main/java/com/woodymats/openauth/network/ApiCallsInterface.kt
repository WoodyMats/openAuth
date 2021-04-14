package com.woodymats.openauth.network

import com.google.gson.JsonObject
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.EnrollmentNetworkEntity
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiCallsInterface {

    @Headers("Content-Type: application/json")
    @POST("checkLogin")
    suspend fun loginUser(@Body entity: LoginEntity): User

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun createAccount(@Body signUpEntity: SignUpEntity): JsonObject

    @Headers("Content-Type: application/json")
    @GET("logout")
    suspend fun logout(@Header("Authorization") token: String)

    @Headers("Content-Type: application/json")
    @GET("allCourses")
    suspend fun getAllCourses(@Header("Authorization") token: String): List<Course>

    @Headers("Content-Type: application/json")
    @GET("getEnrollmentsByUser")
    suspend fun getUserEnrollments(@Header("Authorization") token: String): List<EnrollmentNetworkEntity>

}