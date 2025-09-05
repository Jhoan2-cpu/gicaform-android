package com.example.gica2025.data.network

import com.example.gica2025.BuildConfig
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    private val BASE_URL = BuildConfig.BASE_URL + BuildConfig.API_PATH
    private val WORDPRESS_BASE_URL = BuildConfig.BASE_URL
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val basicAuthInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("Accept", "application/json")
            
        // Solo agregar autenticación si las credenciales no están vacías
        if (BuildConfig.HTTP_USERNAME.isNotEmpty() && BuildConfig.HTTP_PASSWORD.isNotEmpty()) {
            val credentials = Credentials.basic(BuildConfig.HTTP_USERNAME, BuildConfig.HTTP_PASSWORD)
            requestBuilder.addHeader("Authorization", credentials)
        }
        
        chain.proceed(requestBuilder.build())
    }
    
    // FCM también puede necesitar o no basic auth
    private val fcmBasicAuthInterceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            
        // Solo agregar autenticación si las credenciales no están vacías
        if (BuildConfig.HTTP_USERNAME.isNotEmpty() && BuildConfig.HTTP_PASSWORD.isNotEmpty()) {
            val credentials = Credentials.basic(BuildConfig.HTTP_USERNAME, BuildConfig.HTTP_PASSWORD)
            requestBuilder.addHeader("Authorization", credentials)
        }
        
        chain.proceed(requestBuilder.build())
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
    
    // Cliente FCM con basic auth para WordPress
    private val fcmOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(fcmBasicAuthInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val fcmRetrofit = Retrofit.Builder()
        .baseUrl(WORDPRESS_BASE_URL)
        .client(fcmOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val fcmApiService: FCMApiService = fcmRetrofit.create(FCMApiService::class.java)
}