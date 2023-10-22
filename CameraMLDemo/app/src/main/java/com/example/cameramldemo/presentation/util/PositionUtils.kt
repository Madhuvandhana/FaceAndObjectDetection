package com.example.cameramldemo.presentation.util

import android.graphics.PointF
import android.graphics.Rect
import androidx.compose.ui.geometry.Size

fun adjustPoint(point: PointF, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int): PointF {
    val x = point.x / imageWidth * screenWidth
    val y = point.y / imageHeight * screenHeight
    return PointF(x, y)
}

fun adjustSize(size: Size, imageWidth: Int, imageHeight: Int, screenWidth: Int, screenHeight: Int): Size {
    val width = size.width / imageWidth * screenWidth
    val height = size.height / imageHeight * screenHeight
    return Size(width, height)
}

fun getAdjustMirroredBoundingBox(
    boundingBox: androidx.compose.ui.geometry.Rect,
    imageWidth: Int,
    imageHeight: Int,
    screenWidth: Int,
    screenHeight: Int
): Rect {
    val horizontalScaleFactor = screenWidth / imageWidth.toFloat()
    val verticalScaleFactor = screenHeight / imageHeight.toFloat()

    val adjustedBoundRect = Rect()
    adjustedBoundRect.top = (boundingBox.top * verticalScaleFactor).toInt()
    adjustedBoundRect.left = (boundingBox.left * horizontalScaleFactor).toInt()
    adjustedBoundRect.right = (boundingBox.right * horizontalScaleFactor).toInt()
    adjustedBoundRect.bottom = (boundingBox.bottom * verticalScaleFactor).toInt()

    val adjustedMirrorObjectBound = Rect(adjustedBoundRect)

    val originalRight = adjustedBoundRect.right
    val originalLeft = adjustedBoundRect.left
    // mirror the coordination since it's the front facing camera
    adjustedMirrorObjectBound.left = (screenWidth - originalRight)
    adjustedMirrorObjectBound.right = (screenWidth - originalLeft)
    return adjustedMirrorObjectBound
}
