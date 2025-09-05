package com.example.gica2025.data.network

import com.example.gica2025.BuildConfig
import com.example.gica2025.data.model.FCMTokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FCMApiService {
    
    @FormUrlEncoded
    @POST("wp-content/plugins/GICAACCOUNT/mobile-api-public.php")
    suspend fun registerFCMToken(
        @Field("action") action: String = "gica_mobile_register_token",
        @Field("api_key") apiKey: String = BuildConfig.FCM_API_KEY,
        @Field("fcm_token") fcmToken: String,
        @Field("device_id") deviceId: String,
        @Field("device_info") deviceInfo: String,
        @Field("app_version") appVersion: String,
        @Field("user_id") userId: String = ""
    ): Response<FCMTokenResponse>

    @FormUrlEncoded
    @POST("wp-content/plugins/GICAACCOUNT/mobile-api-public.php")
    suspend fun getNotifications(
        @Field("action") action: String = "gica_mobile_get_notifications",
        @Field("api_key") apiKey: String = BuildConfig.FCM_API_KEY,
        @Field("device_id") deviceId: String,
        @Field("limit") limit: String = "20"
    ): Response<FCMTokenResponse>
}