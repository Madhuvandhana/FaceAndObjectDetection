package com.example.cameramldemo.presentation.util

import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

/**
 * Takes a photo from camera and stores it in External storage
 *
 * @param filenameFormat
 * @param imageCapture
 * @param outputDirectory
 * @param executor
 * @param onImageCaptured
 * @param onError
 */

fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
) {
    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg",
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraMLDemoTag", "Take photo error:", exception)
                onError(exception)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                onImageCaptured(savedUri)
            }
        },
    )
}
