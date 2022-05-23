package com.vincent.givetake.data.source.response.items

data class MyWishlistResponse(
    val status: String,
    val data: List<WishlistItem>,
    val message: String
)

data class WishlistItem(
    val id: String,
    val thumbnail: String,
    val distance: String,
    val maxRadius: String
)