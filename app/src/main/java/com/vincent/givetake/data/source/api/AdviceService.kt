package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.request.AddAdviceRequest
import com.vincent.givetake.data.source.response.advice.AddAdviceResponse
import com.vincent.givetake.data.source.response.advice.AllAdviceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AdviceService {
    @POST("advice")
    suspend fun sendAdvice(
        @Header("Authorization") auth: String,
        @Body body: AddAdviceRequest
    ) : Response<AddAdviceResponse>

    @GET("myadvices")
    suspend fun myAdvices(
        @Header("Authorization") auth: String
    ) : Response<AllAdviceResponse>

}