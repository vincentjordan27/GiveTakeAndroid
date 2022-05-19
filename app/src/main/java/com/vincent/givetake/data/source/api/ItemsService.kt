package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.request.AddItemRequest
import com.vincent.givetake.data.source.request.WishlistRequest
import com.vincent.givetake.data.source.request.DeleteItemImageRequest
import com.vincent.givetake.data.source.request.EditItemRequest
import com.vincent.givetake.data.source.response.items.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ItemsService {
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("itemsLog")
    suspend fun getAllItemsLogin(
        @Header("Authorization") auth: String,
    ) : AllItemResponse

    @GET("item/{id}")
    suspend fun getItemById(
        @Path("id") id: String,
        @Header("Authorization") auth: String
    ) : Response<DetailResponseLogin>

    @GET("/itemid")
    suspend fun generateId() : GenerateIdResponse

    @POST("item")
    suspend fun addItem(
        @Body item: AddItemRequest,
        @Header("Authorization") auth: String
    ) : Response<AddItemResponse>

    @DELETE("item/{id}")
    suspend fun deleteItem(
        @Path("id") id: String,
        @Header("Authorization") auth: String
    ) : Response<DeleteItemResponse>

    @Multipart
    @POST("/upload/item/image/{id}")
    suspend fun uploadItemImage(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
        @Part data : MultipartBody.Part
    ) : Response<AddItemImageResponse>

    @HTTP(method = "DELETE", path = "/upload/item/image/{id}", hasBody = true)
    suspend fun deleteItemImage(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
        @Body item: DeleteItemImageRequest
    ) : DeleteItemImageResponse

    @PATCH("/item/{id}")
    suspend fun updateItem(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
        @Body item: EditItemRequest
    ) : Response<UpdateItemResponse>

    @GET("myoffers")
    suspend fun getMyOffers(
        @Header("Authorization") auth: String,
    ) : Response<MyOffersResponse>

    @GET("myitems")
    suspend fun getMyItems(
        @Header("Authorization") auth: String,
    ) : Response<MyItemsResponse>

    @POST("wishlist")
    suspend fun addWishlist(
        @Header("Authorization") auth: String,
        @Body body: WishlistRequest
    ) : Response<WishlistResponse>

    @HTTP(method = "DELETE", path = "wishlist", hasBody = true)
    suspend fun deleteWishlist(
        @Header("Authorization") auth: String,
        @Body body: WishlistRequest
    ) : Response<WishlistResponse>

}