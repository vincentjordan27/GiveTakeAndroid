package com.vincent.givetake.ui.activity.items.add.model

import android.net.Uri

data class ImageData(
    val uri: Uri?,
    val imageId: String = "",
    val url: String = "",
)