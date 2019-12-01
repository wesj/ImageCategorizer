package com.example.myapplication

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureConfig
import androidx.camera.core.UseCase
import java.io.File
import java.util.concurrent.Executor

abstract class Callback : PermissionManager.Callback {
    abstract fun didSaveImage(file: File)
    abstract fun errorSavingImage(msg: String)
    override fun onGranted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRefused() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class CaptureBuilder {
    private var callback: Callback? = null
    private val captureUseCase: ImageCapture by lazy {
        ImageCapture(imageCaptureConfig)
    }

    val imageCaptureConfig = ImageCaptureConfig.Builder()
        .apply {
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
        }.build()

    fun buildUseCase(): UseCase {
        return captureUseCase
    }

    fun capturePicture(dir: File,
                       executor: Executor
    ) {
        val file = File(dir, "${System.currentTimeMillis()}.jpg")

        captureUseCase.takePicture(file, executor,
            object : ImageCapture.OnImageSavedListener {
                override fun onError(
                    imageCaptureError: ImageCapture.ImageCaptureError,
                    message: String,
                    exc: Throwable?
                ) {
                    callback?.errorSavingImage(message)
                }

                override fun onImageSaved(file: File) {
                    val msg = "Photo capture succeeded: ${file.absolutePath}"
                    callback?.didSaveImage(file)
                }
            })
    }

}
