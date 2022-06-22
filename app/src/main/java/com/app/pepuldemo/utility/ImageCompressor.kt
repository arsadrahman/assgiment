package com.app.pepuldemo.utility

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class ImageCompressor {
   public fun compressorBy50Percentage(file: File) :File{
        val compressionRatio =
            50
        try {
            val bitmap = BitmapFactory.decodeFile(file.path)
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                compressionRatio,
                FileOutputStream(file)
            )
        } catch (t: Throwable) {
            Log.e("ERROR", "Error compressing file.$t")
            t.printStackTrace()
        }
        return file;
    }
}