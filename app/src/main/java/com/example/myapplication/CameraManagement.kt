package com.example.myapplication

import android.os.Build
import android.view.TextureView
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraX
import androidx.camera.core.UseCase
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executor

private class DefaultCameraManagementSystemApi: CameraManagement.SystemApi {
    override fun bindToLifecycle(liveCycleOwner: LifecycleOwner,
                                 preview: UseCase,
                                 capture: UseCase,
                                 analyzer: UseCase) {
        CameraX.unbindAll()
        CameraX.bindToLifecycle(liveCycleOwner, preview, capture, analyzer)
    }
}

// TODO: This is probably not the best default executor for this task. Just trying to get things up and running
@RequiresApi(Build.VERSION_CODES.N)
class CameraManagement(private val captureManager: CaptureBuilder,
                       private val previewManager: PreviewBuilder = PreviewBuilder(),
                       private val analyzerManager: AnalyzerBuilder,
                       private val executor: Executor,
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

    fun updateTransform(viewFinder: TextureView) {
        previewManager.updateTransform(viewFinder)
    }
}