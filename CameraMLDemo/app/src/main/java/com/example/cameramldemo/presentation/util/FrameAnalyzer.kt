package com.example.cameramldemo.presentation.util

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.cameramldemo.presentation.util.BitmapUtils.getCroppedBitMap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.io.File

private const val MIN_FACE_SIZE = 0.20f
class FrameAnalyzer(
    private val onFaceDetected: (
        faces: List<Face>,
        width: Int,
        height: Int,
    ) -> Unit,
    private val onObjectsDetected: (
        objects: List<DetectedObject>,
        width: Int,
        height: Int,
    ) -> Unit,
    private val outputDirectory: File,
    private val onSaveBitmap: (
        bitmap: Bitmap,
        fileName: String,
        outputDirectory: File,
    ) -> Unit,
) :
    ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(MIN_FACE_SIZE)
        .enableTracking()
        .build()

    private val faceDetector = FaceDetection.getClient(realTimeOpts)

    private val options = ObjectDetectorOptions.Builder()
        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .build()

    var hasNextDetection = false

    private val objectDetector = ObjectDetection.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val frameMetadata = FrameMetadata.Builder()
                .setWidth(imageProxy.width)
                .setHeight(imageProxy.height)
                .setRotation(imageProxy.imageInfo.rotationDegrees)
                .build()

            if (!hasNextDetection) {
                faceDetector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        if (faces.isNotEmpty()) {
                            onFaceDetected(faces, inputImage.width, inputImage.height)
                            val croppedBitmap = getCroppedBitMap(imageProxy, frameMetadata)

                            if (croppedBitmap != null) {
                                onSaveBitmap(
                                    croppedBitmap,
                                    System.currentTimeMillis().toString() + ".png",
                                    outputDirectory,
                                )
                            }
                        }
                        hasNextDetection = true
                        imageProxy.close()
                    }
                    .addOnFailureListener {
                        imageProxy.close()
                    }
            } else {
                objectDetector.process(inputImage)
                    .addOnSuccessListener { objects ->
                        if (objects.isNotEmpty()) {
                            onObjectsDetected(objects, inputImage.width, inputImage.height)
                            val croppedBitmap = getCroppedBitMap(imageProxy, frameMetadata)

                            if (croppedBitmap != null) {
                                onSaveBitmap(
                                    croppedBitmap,
                                    System.currentTimeMillis().toString() + ".png",
                                    outputDirectory,
                                )
                            }
                        }
                        hasNextDetection = false
                        imageProxy.close()
                    }.addOnFailureListener {
                        imageProxy.close()
                    }
            }
        }
    }
}
