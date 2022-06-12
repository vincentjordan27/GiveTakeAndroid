package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.request.OtpRequest
import com.vincent.givetake.data.source.response.otp.OtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OtpService {
    @Headers("API-Key: c0c6bb0c0ddaa21f7764f4d9bfd198e85d109d79cd923f4842f4b15c7f187f0c")
    @POST("send_whatsapp")
    suspend fun sendOtp(
        @Body body: OtpRequest
    ): Response<OtpResponse>

}