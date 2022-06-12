package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.request.ResetPhoneRequest
import com.vincent.givetake.data.source.response.items.StatusResponse
import retrofit2.Response
import retrofit2.http.*

interface EmailService {
    @POST("resetphone")
    suspend fun resetPhone(
        @Header("Authorization") auth: String,
        @Body body: ResetPhoneRequest
    ): Response<StatusResponse>

}