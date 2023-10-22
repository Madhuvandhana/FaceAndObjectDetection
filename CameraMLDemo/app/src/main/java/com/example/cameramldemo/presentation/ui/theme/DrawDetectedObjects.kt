package com.example.cameramldemo.presentation.ui.theme

import android.graphics.PointF
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toComposeRect
import com.example.cameramldemo.presentation.util.adjustPoint
import com.example.cameramldemo.presentation.util.adjustSize
import com.example.cameramldemo.presentation.util.drawBounds
import com.example.cameramldemo.presentation.util.getAdjustMirroredBoundingBox
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.objects.DetectedObject

private const val STROKE_WIDTH = 10f

@Composable
fun DrawDetectedObjects(
    objects: List<DetectedObject>,
    faces: List<Face>,
    imageFaceWidth: Int,
    imageFaceHeight: Int,
    imageWidth: Int,
    imageHeight: Int,
    screenWidth: Int,
    screenHeight: Int,
    isCameraFrontFacing: Boolean,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        objects.forEach {
            setBounds(it.boundingBox, imageWidth, imageHeight, screenWidth, screenHeight, isCameraFrontFacing)
        }

        faces.forEach {
            setBounds(it.boundingBox, imageFaceWidth, imageFaceHeight, screenWidth, screenHeight, isCameraFrontFacing)
        }
    }
}

fun DrawScope.setBounds(
    rect: Rect,
    imageWidth: Int,
    imageHeight: Int,
    screenWidth: Int,
    screenHeight: Int,
    isCameraFrontFacing: Boolean,
) {
    val boundingBox = rect.toComposeRect()
    val adjustedBoundingBox = if (isCameraFrontFacing) {
        getAdjustMirroredBoundingBox(
            boundingBox,
            imageWidth,
            imageHeight,
            screenWidth,
            screenHeight,
        ).toComposeRect()
    } else {
        boundingBox
    }
    val topLeft = adjustPoint(
        PointF(
            adjustedBoundingBox.topLeft.x,
            adjustedBoundingBox.topLeft.y,
        ),
        imageWidth,
        imageHeight,
        screenWidth,
        screenHeight,
    )

    val size = if (!isCameraFrontFacing) {
        adjustSize(adjustedBoundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)
    } else {
        adjustedBoundingBox.size
    }

    drawBounds(topLeft, size, Color.Yellow, STROKE_WIDTH)
}
