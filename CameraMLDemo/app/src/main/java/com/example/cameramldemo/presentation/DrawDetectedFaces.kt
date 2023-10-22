package com.example.cameramldemo.presentation

import android.graphics.PointF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeRect
import com.example.cameramldemo.presentation.util.adjustPoint
import com.example.cameramldemo.presentation.util.adjustSize
import com.example.cameramldemo.presentation.util.drawBounds
import com.google.mlkit.vision.face.Face
private const val STROKE_WIDTH = 10f

@Composable
fun DrawDetectedFaces(
    faces: List<Face>,
    imageWidth: Int,
    imageHeight: Int,
    screenWidth: Int,
    screenHeight: Int,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        faces.forEach {
            val boundingBox = it.boundingBox.toComposeRect()
            val topLeft = adjustPoint(
                PointF(
                    boundingBox.topLeft.x,
                    boundingBox.topLeft.y,
                ),
                imageWidth,
                imageHeight,
                screenWidth,
                screenHeight,
            )
            val size = adjustSize(boundingBox.size, imageWidth, imageHeight, screenWidth, screenHeight)

            drawBounds(topLeft, size, Color.Yellow, STROKE_WIDTH)
        }
    }
}
