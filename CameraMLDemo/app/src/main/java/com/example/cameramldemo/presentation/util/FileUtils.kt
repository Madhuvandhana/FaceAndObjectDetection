package com.example.cameramldemo.presentation.util

import android.graphics.Bitmap
import android.util.Log
import com.example.cameramldemo.di.IoDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CoroutineDispatcher

suspend fun onSaveBitmap(
    bitmap: Bitmap,
    fileName: String,
    outputDirectory: File,
    @IoDispatcher dispatcher: CoroutineDispatcher,
) = withContext(dispatcher) {
    val file = File(outputDirectory, fileName)

    if (file.exists()) {
        file.delete()
    }
    try {
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 99, out)
        out.flush()
        out.close()
    } catch (e: Exception) {
        Log.e("Exception", e.message.toString())
    }
}
