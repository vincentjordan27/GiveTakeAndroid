package com.vincent.givetake.data.source.response.otp

data class OtpResponse(
    val status: String,
    val error: ErrorData,
    val data: SuccessData
)

data class ErrorData(
    val message: String,
)

data class SuccessData(
    val success: Boolean
)
