package com.example.notewise.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

object RetrofitTextInstance {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api-inference.huggingface.co")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: HuggingFaceTextApi = retrofit.create(HuggingFaceTextApi::class.java)
}
