package com.example.cameramldemo.presentation

import android.graphics.Bitmap
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.example.cameramldemo.di.IoDispatcher
import com.example.cameramldemo.presentation.common.BaseViewModel
import com.example.cameramldemo.presentation.util.onSaveBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class FaceAndObjectDetectionViewModel @Inject constructor(@IoDispatcher val dispatcher: CoroutineDispatcher) :
    BaseViewModel<ViewIntent, FaceAndObjectDetectionViewState, SingleEvent>(),
    DefaultLifecycleObserver {
    override val viewState: StateFlow<FaceAndObjectDetectionViewState>

    init {
        val initialVS = FaceAndObjectDetectionViewState.initial()

        viewState = merge(
            intentSharedFlow.filterIsInstance<ViewIntent.Initial>().take(1),
            intentSharedFlow.filterNot { it is ViewIntent.Initial },
        )
            .shareWhileSubscribed()
            .toPartialStateChangeFlow()
            .sendSingleEvent()
            .scan(initialVS) { vs, change -> change.reduce(vs) }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                initialVS,
            )
    }

    private fun Flow<PartialStateChange>.sendSingleEvent(): Flow<PartialStateChange> {
        return onEach { change ->
            val event = when (change) {
                is PartialStateChange.CameraDetection.OnObjectsDetected -> SingleEvent.OnObjectsDetectedSuccess(
                    change.objects,
                    change.width,
                    change.height,
                )
                is PartialStateChange.CameraDetection.OnFaceDetected -> SingleEvent.OnFaceDetectedSuccess(
                    change.faces,
                    change.width,
                    change.height,
                )
                is PartialStateChange.CameraDetection.SaveBitMapToFile -> SingleEvent.OnSaveBitMapSuccess
            }
            sendEvent(event)
        }
    }

    private fun SharedFlow<ViewIntent>.toPartialStateChangeFlow(): Flow<PartialStateChange> {
        val faceDetectionFlow = filterIsInstance<ViewIntent.OnFaceDetectionSuccess>()
            .distinctUntilChanged()
            .shareWhileSubscribed()
        val objectDetectionFlow = filterIsInstance<ViewIntent.OnObjectsDetectionSuccess>()
            .distinctUntilChanged()
            .shareWhileSubscribed()

        return merge(
            faceDetectionFlow.map { PartialStateChange.CameraDetection.OnFaceDetected(it.faces, it.width, it.height) },
            objectDetectionFlow.map {
                PartialStateChange.CameraDetection.OnObjectsDetected(
                    it.objects,
                    it.width,
                    it.height,
                )
            },
            filterIsInstance<ViewIntent.OnSaveBitMap>().toSaveBitMapToFileFlow(),
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun Flow<ViewIntent.OnSaveBitMap>.toSaveBitMapToFileFlow():
        Flow<PartialStateChange.CameraDetection.SaveBitMapToFile> =
        flatMapLatest {
            saveImage(it.bitmap, it.fileName, it.outputDirectory)
        }

    private fun saveImage(
        bitmap: Bitmap,
        fileName: String,
        outputDirectory: File,
    ) = flow<PartialStateChange.CameraDetection.SaveBitMapToFile> {
        viewModelScope.launch {
            viewState.value.detectedFaces.forEach {
                // TODO will be replaced with unique id from facial recognition
                onSaveBitmap(bitmap, it.trackingId.toString() + ".png", outputDirectory, dispatcher)
            }
            viewState.value.detectedObjects.forEach {
                // TODO will be replaced with unique id from object recognition
                onSaveBitmap(
                    bitmap,
                    System.currentTimeMillis().toString() +
                        it.trackingId.toString() + ".png",
                    outputDirectory,
                    dispatcher,
                )
            }
        }
    }
}
