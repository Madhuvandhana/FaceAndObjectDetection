package com.example.cameramldemo.presentation

import android.graphics.Bitmap
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cameramldemo.R
import com.example.cameramldemo.presentation.ui.theme.DrawDetectedObjects
import com.example.cameramldemo.presentation.util.FrameAnalyzer
import com.example.cameramldemo.presentation.util.ObjectDetectionAnalyzer
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import java.io.File
import java.util.concurrent.Executor

private const val IMAGE_WIDTH = 480
private const val IMAGE_HEIGHT = 640

@Composable
fun MultiBoxTrackerComponent(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    isFaceDetection: Boolean,
    viewState: FaceAndObjectDetectionViewState,
    onObjectsDetected: (objects: List<DetectedObject>, width: Int, height: Int) -> Unit,
    onFaceDetected: (faces: List<Face>, width: Int, height: Int) -> Unit,
    onSaveBitmap: (bitmap: Bitmap, fileName: String, outputDirectory: File) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val screenWidth = remember { mutableStateOf(context.resources.displayMetrics.widthPixels) }
    val screenHeight = remember { mutableStateOf(context.resources.displayMetrics.heightPixels) }

    val lensFacing = remember {
        mutableStateOf(CameraSelector.LENS_FACING_FRONT)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        CameraView(
            analyzer = if (isFaceDetection.not()) {
                ObjectDetectionAnalyzer { objects, width, height ->
                    onObjectsDetected.invoke(objects, width, height)
                }
            } else {
                FrameAnalyzer(
                    onFaceDetected = { faces, width, height ->
                        onFaceDetected.invoke(faces, width, height)
                    },
                    onObjectsDetected = { objects, width, height ->
                        onObjectsDetected.invoke(objects, width, height)
                    },
                    outputDirectory = outputDirectory,
                    onSaveBitmap = onSaveBitmap,
                )
            },
            onImageCaptured = onImageCaptured,
            onError = onError,
            executor = executor,
            outputDirectory = outputDirectory,
            lensFacing = lensFacing.value,
            onCameraSwitch = {
                lensFacing.value = it
            }
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                IconButton(onClick = { /* no-op*/ }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "back",
                        tint = Color.White,
                    )
                }
                Text(
                    text = stringResource(id = R.string.face_object_detection_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                )
            }

            DrawDetectedObjects(
                viewState.detectedObjects,
                viewState.detectedFaces,
                viewState.imageFaceWidth,
                viewState.imageFaceHeight,
                viewState.imageWidth,
                viewState.imageHeight,
                screenWidth.value,
                screenHeight.value,
                isCameraFrontFacing = lensFacing.value == CameraCharacteristics.LENS_FACING_FRONT
            )
        }
    }
}
