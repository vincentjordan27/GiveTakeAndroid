package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.response.fcm.FcmResponse
import com.vincent.givetake.firebase.NotificationData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface NotificationService {
    @POST("send")
    fun sendMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body messageBody: NotificationData
    ) : Call<FcmResponse>
}