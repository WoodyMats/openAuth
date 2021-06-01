package com.woodymats.openauth.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL: String = "http://167.99.40.193/api/"

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofitClient: Retrofit.Builder by lazy {
        val okHttpClient = OkHttpClient.Builder()

        // TODO(Add custom interceptor)
        //okHttpClient.addInterceptor()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create(gson))
    }

    val apiInterface: ApiCallsInterface by lazy {
        retrofitClient
            .build()
            .create(ApiCallsInterface::class.java)
    }
}