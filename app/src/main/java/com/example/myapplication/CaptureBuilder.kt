package com.example.myapplication

import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureConfig
import androidx.camera.core.UseCase
import java.io.File
import java.util.concurrent.Executor

class CaptureBuilder(val callback: Callback) {
    interface Callback {
        fun didSaveImage(file: File)
        fun errorSavingImage(msg: String)
    }

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
                       executor: Executor) {
        val file = File(dir, "${System.currentTimeMillis()}.jpg")

        captureUseCase.takePicture(file, executor,
            object : ImageCapture.OnImageSavedListener {
                override fun onError(
                    imageCaptureError: ImageCapture.ImageCaptureError,
                    message: String,
                    exc: Throwable?
                ) {
                    callback.errorSavingImage(message)
                }

                override fun onImageSaved(file: File) {
                    callback.didSaveImage(file)
                }
            })
    }

}
