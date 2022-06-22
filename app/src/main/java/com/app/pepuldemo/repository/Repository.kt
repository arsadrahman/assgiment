package com.app.pepuldemo.repository

import com.app.pepuldemo.model.DeleteItem
import okhttp3.MultipartBody
import javax.inject.Inject

class Repository @Inject constructor(private val networkInterface: NetworkInterface) {

    //Upload File
    suspend fun uploadMedia(file:MultipartBody.Part) = networkInterface.uploadMedia(file)

    //List Data
    suspend fun listData(lastId:String = "") = networkInterface.getList(lastId)

    //Delete Item
    suspend fun delete(id:String) = networkInterface.deleteItem(DeleteItem(id))

}