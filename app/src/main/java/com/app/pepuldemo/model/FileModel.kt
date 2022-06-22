package com.app.pepuldemo.model

import android.content.Context
import android.net.Uri
import android.util.Log
import com.app.pepuldemo.utility.Validator.Companion.uriToTempFile
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import android.graphics.Bitmap

import android.graphics.BitmapFactory
import com.app.pepuldemo.utility.ImageCompressor
import java.io.FileOutputStream


data class FileModel(val fileUri: Uri? = null,val file_: File? = null, val isVideo: Boolean = false) {
    public fun getMultipartFromUri(mContext: Context): MultipartBody.Part? {
        var file: File? = null
        fileUri?.let { uri ->
            try {
                file_?.let {
                    file = it
                }
                fileUri?.let {
                 file = uri.uriToTempFile(
                    mContext,
                )
                }
                val type =
                    if (!isVideo) MediaType.parse("video/mp4") else MediaType.parse("image/jpg");
                val requestFile: RequestBody? =
                    uri.uriToTempFile(
                        mContext
                    )?.let { file ->
                            RequestBody.create(
                                type,
                                file
                            );

                    }
                val body: MultipartBody.Part? = requestFile?.let { requestBody ->
                    file?.let { file->
                    (file.name)?.split('.')?.get(1)?.let { it1 ->
                        MultipartBody.Part.createFormData(
                            "fileToUpload",
                            file.name,
                            requestBody
                        )
                    }
                }}

                return body
            } catch (exp: Exception) {
                Log.e("TAG", exp.message!!)
            }
        }
        return null

    }


}
