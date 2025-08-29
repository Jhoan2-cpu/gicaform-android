package com.example.gica2025.data.network

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val BASE_URL = "https://ornery-fog.localsite.io/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val basicAuthInterceptor = Interceptor { chain ->
        val credentials = Credentials.basic("beginner", "euphoric")
        val request = chain.request().newBuilder()
            .addHeader("Authorization", credentials)
            .build()
        chain.proceed(request)
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(basicAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val authApiService: AuthApiService = retrofit.create(AuthApiService::class.java)
}