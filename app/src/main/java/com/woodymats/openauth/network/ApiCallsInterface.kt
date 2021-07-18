package com.woodymats.openauth.network

import com.google.gson.JsonObject
import com.woodymats.openauth.models.EnrollToCourseModel
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.models.local.UserEntity
import com.woodymats.openauth.models.remote.ChapterNetworkEntity
import com.woodymats.openauth.models.remote.CourseNetworkEntity
import com.woodymats.openauth.models.remote.EnrollmentNetworkEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiCallsInterface {

    @Headers("Content-Type: application/json")
    @POST("checkLogin")
    suspend fun loginUser(@Body entity: LoginEntity): UserEntity

    @Headers("Content-Type: application/json")
    @POST("sendDeviceToken")
    suspend fun sendDeviceId(
        @Header("Authorization") token: String,
        @Query("deviceId") deviceId: String
    )

    @Headers("Content-Type: application/json")
    @POST("register")
    suspend fun createAccount(@Body signUpEntity: SignUpEntity): JsonObject

    @Headers("Content-Type: application/json")
    @GET("logout")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Query("logoutFromAndroid") logoutFromAndroid: Boolean
    ): JsonObject

    @Headers("Content-Type: application/json")
    @GET("allCourses")
    suspend fun getAllCourses(@Header("Authorization") token: String): List<CourseNetworkEntity>

    @Headers("Content-Type: application/json")
    @GET("getEnrollmentsByUser")
    suspend fun getUserEnrollments(@Header("Authorization") token: String): List<EnrollmentNetworkEntity>

    @Headers("Content-Type: application/json")
    @POST("enrollToCourse")
    suspend fun enrollToCourse(
        @Header("Authorization") token: String,
        @Body enrollToCourseModel: EnrollToCourseModel
    ): List<ChapterNetworkEntity>

    // @JvmSuppressWildcards
    @Multipart
    @POST("register")
    suspend fun createAccountWithProfileImage(
        // @PartMap map: Map<String, RequestBody>
        @Part file: MultipartBody.Part,
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part("canCreateCourses") canCreateCourses: RequestBody,
    ): JsonObject

    @Multipart
    @POST("updateUser")
    suspend fun updateUserWithFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("firstName") firstName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody
    ): UserEntity?

    @POST("updateUser")
    suspend fun updateUserWithoutFile(
        @Header("Authorization") token: String,
        @Query("firstName") firstName: String,
        @Query("lastName") lastName: String,
        @Query("profileImage") profileImage: String,
        @Query("dateOfBirth") dateOfBirth: Long
    ): UserEntity?
}
