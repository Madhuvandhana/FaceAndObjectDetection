package com.example.cameramldemo.presentation

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import com.example.cameramldemo.presentation.common.MviIntent
import com.example.cameramldemo.presentation.common.MviSingleEvent
import com.example.cameramldemo.presentation.common.MviViewState
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject
import java.io.File

private const val IMAGE_WIDTH = 480
private const val IMAGE_HEIGHT = 640

@Immutable
data class FaceAndObjectDetectionViewState(
    val detectedObjects: List<DetectedObject>,
    val detectedFaces: List<Face>,
    val imageWidth: Int,
    val imageHeight: Int,
    val imageFaceWidth: Int,
    val imageFaceHeight: Int,
) : MviViewState {
    companion object {
        fun initial() = FaceAndObjectDetectionViewState(
            detectedObjects = emptyList(),
            detectedFaces = emptyList(),
            imageWidth = IMAGE_WIDTH,
            imageHeight = IMAGE_HEIGHT,
            imageFaceWidth = IMAGE_WIDTH,
            imageFaceHeight = IMAGE_HEIGHT,
        )
    }
}

@Immutable
sealed interface ViewIntent : MviIntent {
    object Initial : ViewIntent
    data class OnFaceDetectionSuccess(val faces: List<Face>, val width: Int, val height: Int) : ViewIntent
    data class OnObjectsDetectionSuccess(
        val objects: List<DetectedObject>,
        val width: Int,
        val height: Int,
    ) : ViewIntent

    data class OnSaveBitMap(
        val bitmap: Bitmap,
        val fileName: String,
        val outputDirectory: File,
    ) : ViewIntent
}

sealed interface SingleEvent : MviSingleEvent {
    data class OnFaceDetectedSuccess(val faces: List<Face>, val width: Int, val height: Int) :
        SingleEvent
    data class OnObjectsDetectedSuccess(
        val objects: List<DetectedObject>,
        val width: Int,
        val height: Int,
    ) : SingleEvent

    object OnSaveBitMapSuccess : SingleEvent
}

internal sealed interface PartialStateChange {
    fun reduce(viewState: FaceAndObjectDetectionViewState): FaceAndObjectDetectionViewState

    sealed interface CameraDetection : PartialStateChange {
        override fun reduce(viewState: FaceAndObjectDetectionViewState): FaceAndObjectDetectionViewState {
            return when (this) {
                is OnFaceDetected -> viewState.copy(
                    detectedFaces = faces,
                    detectedObjects = emptyList(),
                    imageFaceHeight = height,
                    imageFaceWidth = width,
                    imageHeight = IMAGE_HEIGHT,
                    imageWidth = IMAGE_WIDTH,
                )

                is OnObjectsDetected -> {
                    viewState.copy(
                        detectedFaces = emptyList(),
                        detectedObjects = objects,
                        imageHeight = height,
                        imageWidth = width,
                        imageFaceHeight = IMAGE_HEIGHT,
                        imageFaceWidth = IMAGE_WIDTH,
                    )
                }
                is SaveBitMapToFile -> {
                    viewState
                }
            }
        }

        data class OnFaceDetected(val faces: List<Face>, val width: Int, val height: Int) : CameraDetection
        data class OnObjectsDetected(
            val objects: List<DetectedObject>,
            val width: Int,
            val height: Int,
        ) : CameraDetection

        data class SaveBitMapToFile(
            val bitmap: Bitmap,
            val fileName: String,
            val outputDirectory: File,
        ) : CameraDetection
    }
}
