package com.example.cameramldemo.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.example.cameramldemo.R
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class FaceAndObjectDetectionActivity : ComponentActivity() {
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)
    private var photoUri: Uri? = null
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (shouldShowCamera.value) {
                Body(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = { Log.e("CameraMLDemoTag", "View error:", it) },
                )
            }
            if (shouldShowPhoto.value) {
                Image(
                    painter = rememberImagePainter(photoUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        requestCameraPermission()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            Log.i("CameraMLDemoTag", "Permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("CameraMLDemoTag", "Permission denied")
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("CameraMLDemoTag", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA,
            ) -> Log.i("CameraMLDemoTag", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun handleImageCapture(uri: Uri) {
        Log.i("CameraMLDemoTag", "Image captured: $uri")
        shouldShowCamera.value = false
        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
