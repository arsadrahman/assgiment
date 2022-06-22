package com.app.pepuldemo.utility

import android.content.Context
import android.os.Environment
import android.R.attr
import android.util.Patterns
import android.os.FileUtils

import android.graphics.BitmapFactory

import android.content.ContentResolver

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.core.content.ContextCompat
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import androidx.core.content.ContextCompat.startActivity
import com.google.gson.Gson
import android.provider.MediaStore

import android.provider.DocumentsContract

import android.content.ContentUris
import android.database.Cursor
import android.R.attr.data
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.core.text.isDigitsOnly

import java.io.File
import java.io.FileOutputStream

class Validator {
    companion object {
        fun createTempFileForUri(fileName: String, mContext: Context): File {
            var storageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            return File.createTempFile(
                (fileName)?.split('.')?.get(0),
                ".${(fileName)?.split('.')?.get(1)}",
                storageDir
            )
        }

        fun Uri.uriToTempFile(mContext: Context): File? {

            mContext.contentResolver.openInputStream(this)?.let { inputStream ->
                val tempFile: File? =
                    this.getOriginalFileName(mContext)
                        ?.let { fileName -> createTempFileForUri(fileName, mContext) }
                val fileOutputStream = FileOutputStream(tempFile)
                inputStream.copyTo(fileOutputStream)
                inputStream.close()
                fileOutputStream.close()
                return tempFile
            }
            return null

        }


        fun String?.isImageFile(context: Context, uri: Uri): Boolean {
            val contentResolver: ContentResolver = context.contentResolver
            val type = contentResolver.getType(uri)
            if (type != null) {
                return type.startsWith("image/")
            } else {
                var inputStream: InputStream? = null
                try {
                    inputStream = contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeStream(inputStream, null, options)
                        return options.outWidth > 0 && options.outHeight > 0
                    }
                } catch (e: IOException) {
                } finally {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.closeQuietly(inputStream)
                    }
                }
            }
            return false
        }

        fun String?.isVideoFile(context: Context, uri: Uri): Boolean {
            val contentResolver: ContentResolver = context.contentResolver
            val type = contentResolver.getType(uri)
            if (type != null)
                return type.startsWith("video/")

            return false
        }



        fun String.isValidFileSize(): Boolean {
            return (this.toInt() <= 200000000) //200000000 Byte - 200 MB
        }

        fun String.isValidPhotoSize(): Boolean {
            return (this.toInt() <= 200000000) //200000000 Byte - 200 MB
        }


        fun Uri.getRealSizeFromUri(context: Context): String? {
            var cursor: Cursor? = null
            return try {
                val proj = arrayOf(MediaStore.Audio.Media.SIZE)
                cursor = context.contentResolver.query(this, proj, null, null, null)
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                cursor.moveToFirst()
                cursor.getString(column_index)
            } finally {
                cursor?.close()
            }
        }
        fun Uri.getOriginalFileName(context: Context): String? {
            return context.contentResolver.query(this, null, null, null, null)?.use {
                val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                it.moveToFirst()
                it.getString(nameColumnIndex)
            }
        }

    }




}