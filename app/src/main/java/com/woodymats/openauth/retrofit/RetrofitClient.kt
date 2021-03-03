package com.woodymats.openauth.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    // TODO(Set the right url when it's ready)
    final val BASE_URL: String = "https://"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofitClient: Retrofit.Builder by lazy {
        val okhttpClient = OkHttpClient.Builder()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttpClient.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
    }

    val apiInterface: ApiCallsInterface by lazy {
        retrofitClient
            .build()
            .create(ApiCallsInterface::class.java)
    }

}