package com.woodymats.openauth.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // TODO(Set the right url when it's ready)
    final val BASE_URL: String = "https://www.google.gr/"

    private val gson = GsonBuilder()
        .create()

    val retrofitClient: Retrofit.Builder by lazy {
        val okhttpClient = OkHttpClient.Builder()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    val apiInterface: ApiCallsInterface by lazy {
        retrofitClient
            .build()
            .create(ApiCallsInterface::class.java)
    }
}