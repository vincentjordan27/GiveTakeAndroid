package com.vincent.givetake.data.source.api

import com.vincent.givetake.data.source.request.*
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

    @POST("filter")
    suspend fun filterItem(
        @Header("Authorization") auth: String,
        @Body body: FilterRequest
    ): Response<AllItemResponse>

    @GET("item/{id}")
    suspend fun getItemById(
        @Path("id") id: String,
        @Header("Authorization") auth: String
    ) : Response<DetailResponseLogin>

    @GET("itemid")
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
    @POST("upload/item/image/{id}")
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

    @PATCH("item/{id}")
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

    @GET("wishlist")
    suspend fun getWishlist(
        @Header("Authorization") auth: String,
    ) : Response<MyWishlistResponse>

    @POST("request/{id}")
    suspend fun requestItem(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
        @Body body: ItemRequestBody
    ) : Response<StatusResponse>

    @DELETE("request/{id}")
    suspend fun deleteRequestItem(
        @Header("Authorization") auth: String,
        @Path("id") itemId: String,
    ) : Response<StatusResponse>

    @GET("choose/{id}")
    suspend fun getReceiver(
        @Header("Authorization") auth: String,
        @Path("id") itemId: String,
    ) : Response<ReceiverListResponse>

    @POST("choose/{id}")
    suspend fun chooseReceiver(
        @Header("Authorization") auth: String,
        @Path("id") itemId: String,
        @Body body: ChooseReceiverRequest
    ) : Response<StatusResponse>

    @Multipart
    @POST("upload/ulasan/image/{id}")
    suspend fun uploadUlasanImage(
        @Header("Authorization") auth: String,
        @Path("id") itemId: String,
        @Part data : MultipartBody.Part
    ) : Response<AddUlasanImageResponse>

    @HTTP(method = "DELETE", path = "upload/ulasan/image/{id}", hasBody = true)
    suspend fun deleteUlasanImage(
        @Header("Authorization") auth: String,
        @Path("id") itemId: String,
        @Body body: DeleteUlasanImageRequest
    ) : Response<StatusResponse>

    @POST("receive/{id}")
    suspend fun finishReceiveItem(
        @Header("Authorization") auth: String,
        @Path("id") itemId: String,
        @Body body: FinishReceiveItemRequest
    ) : Response<StatusResponse>

}