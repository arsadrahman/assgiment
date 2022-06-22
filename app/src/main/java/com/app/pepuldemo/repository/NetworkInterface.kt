package com.app.pepuldemo.repository
import com.app.pepuldemo.model.ApiResponse
import com.app.pepuldemo.model.DeleteItem
import okhttp3.MultipartBody
import okhttp3.Response
import retrofit2.http.*

interface NetworkInterface {

    //Upload Image or Video
    @Multipart
    @POST("uploader.php")
    suspend fun uploadMedia(
        @Part fileToUpload: MultipartBody.Part
    ): ApiResponse

    //Get List
    @FormUrlEncoded
    @POST("select.php")
    suspend fun getList(
        @Field("lastFetchId") lastItemId: String =""
    ): ApiResponse

    //Delete Item
    @POST("delete.php")
    suspend fun deleteItem(
        @Body item: DeleteItem
    ): ApiResponse





}