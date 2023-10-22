package com.example.cameramldemo.domain.entities

import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject

private const val IMAGE_WIDTH = 480
private const val IMAGE_HEIGHT = 640
data class FaceAndObjectDetectionState(
    val detectedObjects: List<DetectedObject> = emptyList(),
    val detectedFaces: List<Face> = emptyList(),
    val imageWidth: Int = IMAGE_WIDTH,
    val imageHeight: Int = IMAGE_HEIGHT,
    val imageFaceWidth: Int = IMAGE_WIDTH,
    val imageFaceHeight: Int = IMAGE_HEIGHT
)