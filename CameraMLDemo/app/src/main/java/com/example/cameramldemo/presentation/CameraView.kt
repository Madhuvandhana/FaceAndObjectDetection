package com.example.cameramldemo.presentation

import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Cameraswitch
import androidx.compose.material.icons.sharp.Lens
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cameramldemo.presentation.util.getCameraProvider
import com.example.cameramldemo.presentation.util.takePhoto
import java.io.File
import java.util.concurrent.Executor

private const val SCREEN_RESOLUTION_WIDTH = 1280
private const val SCREEN_RESOLUTION_HEIGHT = 720

@Composable
fun CameraView(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    analyzer: ImageAnalysis.Analyzer,
    lensFacing: Int,
    onCameraSwitch: (lensFacing: Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetResolution(
            Size(
                SCREEN_RESOLUTION_WIDTH,
                SCREEN_RESOLUTION_HEIGHT,
            ),
        )
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
    imageAnalysis.setAnalyzer(
        executor,
        analyzer,
    )

    //TODO To Be made lifecycle-aware
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            imageAnalysis,
            preview,
            imageCapture,
        )

        preview.setSurfaceProvider(previewView.surfaceProvider)
    }

    // 3
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
        AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

        IconButton(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = {
                takePhoto(
                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    onImageCaptured = onImageCaptured,
                    onError = onError,
                )
            },
            content = {
                Icon(
                    imageVector = Icons.Sharp.Lens,
                    contentDescription = "Take picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(1.dp)
                        .border(1.dp, Color.White, CircleShape),
                )
            },
        )

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = {
                if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    onCameraSwitch.invoke(CameraCharacteristics.LENS_FACING_BACK)
                } else {
                    onCameraSwitch.invoke(CameraCharacteristics.LENS_FACING_FRONT)
                }
            },
            containerColor = MaterialTheme.colorScheme.secondary,
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(
                imageVector = Icons.Sharp.Cameraswitch,
                contentDescription = "Camera Switch",
                tint = Color.White,
            )
        }
    }
}
