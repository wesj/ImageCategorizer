package com.example.myapplication

import android.content.Context
import android.graphics.Matrix
import android.os.Build
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.FirebaseApp
import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

private class DefaultCameraManagementSystemApi: CameraManagement.SystemApi {
    override fun bindToLifecycle(liveCycleOwner: LifecycleOwner,
                                 preview: UseCase,
                                 capture: UseCase,
                                 analyzer: UseCase) {
        CameraX.bindToLifecycle(liveCycleOwner, preview, capture, analyzer)
    }
}

// TODO: This is probably not the best default executor for this task. Just trying to get things up and running
@RequiresApi(Build.VERSION_CODES.N)
class CameraManagement(cameraCallback: CaptureBuilder.Callback,
                       private val captureManager: CaptureBuilder = CaptureBuilder(cameraCallback),
                       private val previewManager: PreviewBuilder = PreviewBuilder(),
                       private val analyzerManager: AnalyzerBuilder = AnalyzerBuilder(),
                       private val executor: Executor = Executors.newWorkStealingPool(4),
                       private val systemApi: SystemApi = DefaultCameraManagementSystemApi()) {

    interface SystemApi {
        fun bindToLifecycle(liveCycleOwner: LifecycleOwner,
                            preview: UseCase,
                            capture: UseCase,
                            analyzer: UseCase)
    }

    fun startCamera(liveCycleOwner: LifecycleOwner,
                    textureView: TextureView) {
        val analyzerUseCase = analyzerManager.buildUseCase(executor)
        val captureUseCase = captureManager.buildUseCase()
        val previewUseCase = previewManager.buildUseCase(textureView)
        systemApi.bindToLifecycle(liveCycleOwner, previewUseCase, captureUseCase, analyzerUseCase)
    }

    fun capturePicture(dir: File) {
        captureManager.capturePicture(dir, executor)
    }

    fun updateTransform(viewFinder: TextureView) {
        previewManager.updateTransform(viewFinder)
    }
}