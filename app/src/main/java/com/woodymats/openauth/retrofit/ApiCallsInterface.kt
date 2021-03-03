package com.woodymats.openauth.retrofit

import com.woodymats.openauth.models.LoginEntity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiCallsInterface {

    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginUser(@Body entity: LoginEntity): Call<JSONObject>

}