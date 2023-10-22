package com.example.cameramldemo.presentation.util

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark

private const val MIN_FACE_SIZE = 0.20f
class FaceAnalyzer(
    private val onProcessFaceList: (faces: List<Face>, width: Int, height: Int) -> Unit,
) : ImageAnalysis.Analyzer {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(MIN_FACE_SIZE)
        .enableTracking()
        .build()

    private val highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()

    private val detector = FaceDetection.getClient(highAccuracyOpts)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        mediaImage?.let {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    onProcessFaceList(faces, inputImage.width, inputImage.height)
                    imageProxy.close()
                }
                .addOnFailureListener {
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    /**
     * @see https://firebase.google.com/docs/ml-kit/android/detect-faces
     *
     * @param faces
     */
    fun processFaceList(faces: List<Face>) {
        // [START mlkit_face_list]
        for (face in faces) {
            val bounds = face.boundingBox
            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
            if (leftEar != null) {
                val leftEarPos = leftEar.position
            }

            // If contour detection was enabled:
            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)!!
                .points
            val upperLipBottomContour = face.getContour(FaceContour.UPPER_LIP_BOTTOM)!!
                .points

            // If classification was enabled:
            if (face.smilingProbability != null) {
                val smileProb = face.smilingProbability!!
            }
            if (face.rightEyeOpenProbability != null) {
                val rightEyeOpenProb = face.rightEyeOpenProbability!!
            }

            // If face tracking was enabled:
            if (face.trackingId != null) {
                val id = face.trackingId!!
            }
        }
    }
}
