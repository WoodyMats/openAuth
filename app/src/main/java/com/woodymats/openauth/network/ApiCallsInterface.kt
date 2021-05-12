package com.woodymats.openauth.network

import com.google.gson.JsonObject
import com.woodymats.openauth.models.remote.CourseNetworkEntity
import com.woodymats.openauth.models.EnrollToCourseModel
import com.woodymats.openauth.models.remote.EnrollmentNetworkEntity
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.models.local.UserEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiCallsInterface {

    @Headers("Content-Type: application/json")
    @POST("checkLogin")
    suspend fun loginUser(@Body entity: LoginEntity): UserEntity

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun createAccount(@Body signUpEntity: SignUpEntity): JsonObject

    @Headers("Content-Type: application/json")
    @GET("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("allCourses")
    suspend fun getAllCourses(@Header("Authorization") token: String): List<CourseNetworkEntity>

    @Headers("Content-Type: application/json")
    @GET("getEnrollmentsByUser")
    suspend fun getUserEnrollments(@Header("Authorization") token: String): List<EnrollmentNetworkEntity>

    @Headers("Content-Type: application/json")
    @POST("enrollToCourse")
    suspend fun enrollToCourse(@Header("Authorization") token: String, @Body enrollToCourseModel: EnrollToCourseModel)

}