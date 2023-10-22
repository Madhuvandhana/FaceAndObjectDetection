package com.example.cameramldemo.presentation

import android.Manifest
import android.graphics.Bitmap
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
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import java.io.File
import java.util.concurrent.Executor

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceAndObjectDetectionScreen(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    viewState: FaceAndObjectDetectionViewState,
    onObjectsDetected: (objects: List<DetectedObject>, width: Int, height: Int) -> Unit,
    onFaceDetected: (faces: List<Face>, width: Int, height: Int) -> Unit,
    onSaveBitmap: (bitmap: Bitmap, fileName: String, outputDirectory: File) -> Unit,
) {
    val context = LocalContext.current
    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)

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
            isFaceDetection = true,
            viewState = viewState,
            onObjectsDetected = onObjectsDetected,
            onFaceDetected = onFaceDetected,
            onSaveBitmap = onSaveBitmap,
        )
    }
}
