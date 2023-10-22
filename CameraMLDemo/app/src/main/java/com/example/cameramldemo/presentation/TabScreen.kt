package com.example.cameramldemo.presentation

import android.net.Uri
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.Executor

@Composable
fun Body(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    viewModel: FaceAndObjectDetectionViewModel = hiltViewModel(),
) {
    val intentChannel = remember { Channel<ViewIntent>(Channel.UNLIMITED) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            intentChannel
                .consumeAsFlow()
                .onEach(viewModel::processIntent)
                .collect()
        }
    }

    viewModel.observeLifecycle(LocalLifecycleOwner.current.lifecycle)
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val dispatch = remember {
        { intent: ViewIntent ->
            intentChannel.trySend(intent).getOrThrow()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        FaceAndObjectDetectionScreen(
            outputDirectory = outputDirectory,
            executor = executor,
            onImageCaptured = onImageCaptured,
            onError = onError,
            viewState = viewState,
            onFaceDetected = { faces, width, height ->
                dispatch(ViewIntent.OnFaceDetectionSuccess(faces, width, height))
            },
            onObjectsDetected = { objects, width, height ->
                dispatch(ViewIntent.OnObjectsDetectionSuccess(objects, width, height))
            },
            onSaveBitmap = { bitmap, fileName, outputDirectory ->
                dispatch(ViewIntent.OnSaveBitMap(bitmap, fileName, outputDirectory))
            },
        )
    }
}

@Composable
fun <LO : LifecycleObserver> LO.observeLifecycle(lifecycle: Lifecycle) {
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(this@observeLifecycle)
        onDispose {
            lifecycle.removeObserver(this@observeLifecycle)
        }
    }
}
