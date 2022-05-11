package com.vincent.givetake.data.source.request

data class UpdateProfileRequest(
    var name: String,
    var oldpassword: String,
    var newpassword: String,
    var latitude: String,
    var longitude: String,
    var address: String,
    var photo: String
)
