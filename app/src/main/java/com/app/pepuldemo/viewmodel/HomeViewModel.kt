package com.app.pepuldemo.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.pepuldemo.model.ApiResponse
import com.app.pepuldemo.repository.Repository
import com.iceteck.silicompressorr.SiliCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.net.ConnectException
import javax.inject.Inject
import android.os.Environment

import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.liveData
import com.app.pepuldemo.model.Data
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val compressor: SiliCompressor,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var isUploading: MutableLiveData<Boolean> = MutableLiveData()
    var listData:MutableLiveData<ArrayList<Data>> = MutableLiveData()
    var deletedData:MutableLiveData<Data> = MutableLiveData()
    var errorMessage: MutableLiveData<String> = MutableLiveData()
    var successMessage: MutableLiveData<String> = MutableLiveData()


    private val TYPE_IMAGE = 1
    private val TYPE_VIDEO = 2

    fun deleteItem(data:Data){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                var response: ApiResponse? =
                    data.id?.let { repository.delete(it) }
                response.let { result ->
                    if (result != null) {
                        if (result.statusCode == "200") {
                            isUploading.postValue(false)
                            deletedData.postValue(data)

                            return@launch

                        } else {
                            isUploading.postValue(false)
                            if (response != null) {
                                response.message.let { message ->
                                    errorMessage.postValue(" $message")
                                }
                            }
                            return@launch
                        }
                    }
                }

                isUploading.postValue(false)
                response?.message?.let { message ->
                    errorMessage.postValue("$message")
                }
            }catch (ex: Exception) {
                isUploading.postValue(false)
                if (ex is ConnectException) {
                    errorMessage.postValue("Unable to communicate with server , please check your internet connection")
                } else {
                    errorMessage.postValue(ex.message)
                }
            }
        }
    }
    fun getList(lastId:String = ""){
        viewModelScope.launch(Dispatchers.IO) {
           try{
               var response: ApiResponse =
                repository.listData(lastId)
            response.let { result ->
                if (result.statusCode == "200") {
                    isUploading.postValue(false)

                    listData.postValue(response.data)
                    return@launch

                } else {
                    isUploading.postValue(false)
                    response.message.let { message ->
                        errorMessage.postValue(" $message")
                    }
                    return@launch
                }
            }

            isUploading.postValue(false)
            response.message?.let { message ->
                errorMessage.postValue("$message")
            }
        }catch (ex: Exception) {
            isUploading.postValue(false)
            if (ex is ConnectException) {
                errorMessage.postValue("Unable to communicate with server , please check your internet connection")
            } else {
                errorMessage.postValue(ex.message)
            }
        }
        }
    }

    fun uploadMedia(file: MultipartBody.Part) {
        viewModelScope.launch(Dispatchers.IO) {
            isUploading.postValue(true)
            try {
                var response: ApiResponse =
                    repository.uploadMedia(file)
                response.let { result ->
                    if (result.statusCode == "200") {
                        isUploading.postValue(false)
                        successMessage.postValue(response.message)
                        return@launch

                    } else {
                        isUploading.postValue(false)
                        response.message.let { message ->
                            errorMessage.postValue(" $message")
                        }
                        return@launch
                    }
                }

                isUploading.postValue(false)
                response.message?.let { message ->
                    errorMessage.postValue("$message")
                }
            } catch (ex: Exception) {
                isUploading.postValue(false)
                if (ex is ConnectException) {
                    errorMessage.postValue("Unable to communicate with server , please check your internet connection")
                } else {
                    errorMessage.postValue(ex.message)
                }
            }

        }

    }

    fun compressMedia(mediaUri: Uri, isImage: Boolean) = liveData(Dispatchers.IO) {
        isUploading.postValue(true)
        var file = if (isImage) createMediaFile(TYPE_IMAGE) else createMediaFile(TYPE_VIDEO)
        try {
            if (isImage) {
                var uri = Uri.parse((compressor.compress(mediaUri.toString(), file)))
                isUploading.postValue(false)
                emit(uri)

            } else {
                //val videoResult = compressor.compressVideo(mediaUri.toString(), file?.absolutePath)
                isUploading.postValue(false)
                emit(mediaUri)
            }
        } catch (ex: Exception) {
            ex.message?.let { Log.e("File ", it) }
            isUploading.postValue(false)
        }
        isUploading.postValue(false)


    }

    @Throws(IOException::class)
    private fun createMediaFile(type: Int): File? {

        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName =
            if (type == TYPE_IMAGE) "JPEG_" + timeStamp + "_" else "VID_" + timeStamp + "_"
        val storageDir: File? = context.getExternalFilesDir(
            if (type == TYPE_IMAGE) Environment.DIRECTORY_PICTURES else Environment.DIRECTORY_MOVIES
        )
        val file = File.createTempFile(
            fileName,  /* prefix */
            if (type == TYPE_IMAGE) ".jpg" else ".mp4",
            storageDir
        )

        return file
    }



}