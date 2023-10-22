package com.example.cameramldemo.presentation

import android.net.Uri
import android.widget.Toast
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.objects.DetectedObject
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ObjectDetectionScreen(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    viewState: FaceAndObjectDetectionViewState,
    onObjectsDetected: (objects: List<DetectedObject>, width: Int, height: Int) -> Unit,
) {
    val context = LocalContext.current
    val cameraPermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    PermissionRequired(
        permissionState = cameraPermissionState,
        permissionNotGrantedContent = {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
        },
        permissionNotAvailableContent = {
            Column {
                Toast.makeText(context, "Permission denied.", Toast.LENGTH_LONG).show()
            }
        },
    ) {
        MultiBoxTrackerComponent(
            outputDirectory = outputDirectory,
            executor = executor,
            onImageCaptured = onImageCaptured,
            onError = onError,
            isFaceDetection = false,
            viewState = viewState,
            onObjectsDetected = onObjectsDetected,
            onFaceDetected = { _, _, _ -> /* no-op */ },
            onSaveBitmap = { _, _, _ -> /* no-op */ },
        )
    }
}
