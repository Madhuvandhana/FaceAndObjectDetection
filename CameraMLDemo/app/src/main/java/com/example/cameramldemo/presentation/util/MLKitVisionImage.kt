package com.example.cameramldemo.presentation.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.net.Uri
import android.util.SparseIntArray
import android.view.Surface
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import java.nio.ByteBuffer

/**
 * @see https://github.com/googlesamples/mlkit/tree/master
 */

object MLKitVisionImage {
    private const val TAG = "MLKIT"
    private const val MY_CAMERA_ID = "my_camera_id"

    private val ORIENTATIONS = SparseIntArray()

    private const val ROTATION_90 = 90
    private const val ROTATION_180 = 180
    private const val ROTATION_270 = 270

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 0)
        ORIENTATIONS.append(Surface.ROTATION_90, ROTATION_90)
        ORIENTATIONS.append(Surface.ROTATION_180, ROTATION_180)
        ORIENTATIONS.append(Surface.ROTATION_270, ROTATION_270)
    }

    private fun imageFromBitmap(bitmap: Bitmap) {
        val rotationDegree = 0
        // [START image_from_bitmap]
        val image = InputImage.fromBitmap(bitmap, rotationDegree)
        // [END image_from_bitmap]
    }

    private fun imageFromMediaImage(mediaImage: Image, rotation: Int) {
        // [START image_from_media_image]
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        // [END image_from_media_image]
    }

    private fun imageFromBuffer(byteBuffer: ByteBuffer, rotationDegrees: Int) {
        // [START set_metadata]
        // TODO How do we document the FrameMetadata developers need to implement?
        // [END set_metadata]

        // [START image_from_buffer]
        val image = InputImage.fromByteBuffer(
            byteBuffer, /* image width */
            480, /* image height */
            360,
            rotationDegrees,
            InputImage.IMAGE_FORMAT_NV21, // or IMAGE_FORMAT_YV12
        )
        // [END image_from_buffer]
    }

    private fun imageFromArray(byteArray: ByteArray, rotation: Int) {
        // [START image_from_array]
        val image = InputImage.fromByteArray(
            byteArray, /* image width */
            480, /* image height */
            360,
            rotation,
            InputImage.IMAGE_FORMAT_NV21, // or IMAGE_FORMAT_YV12
        )
        // [END image_from_array]
    }

    fun imageFromPath(context: Context, uri: Uri?): InputImage? {
        // [START image_from_path]
        var image: InputImage? = null
        try {
            image = uri?.let { InputImage.fromFilePath(context, it) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
        // [END image_from_path]
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(
        cameraId: String,
        activity: Activity,
        isFrontFacing: Boolean,
    ): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS[deviceRotation]

        // Get the device's sensor orientation.
        val cameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
            .getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        rotationCompensation = if (isFrontFacing) {
            (sensorOrientation + rotationCompensation) % 360
        } else { // back-facing
            (sensorOrientation - rotationCompensation + 360) % 360
        }
        return rotationCompensation
    }

    // [END get_rotation]
    @Throws(CameraAccessException::class)
    private fun getCompensation(activity: Activity, isFrontFacing: Boolean) {
        // Get the ID of the camera using CameraManager. Then:
        val rotation = getRotationCompensation(MY_CAMERA_ID, activity, isFrontFacing)
    }
}
