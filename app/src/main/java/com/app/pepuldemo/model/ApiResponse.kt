package com.app.pepuldemo.model

import com.google.gson.annotations.SerializedName

data class ApiResponse (
    @SerializedName("statusCode") val statusCode : String?,
    @SerializedName("message") val message : String?,
    @SerializedName("data") val data : ArrayList<Data>?
)
data class Data (
    @SerializedName("result", alternate = ["file"]) val result : String?,
    @SerializedName("file_type") val type : Object?,
    @SerializedName("id") val id : String?
)
