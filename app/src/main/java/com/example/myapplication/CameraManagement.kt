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

class CameraManagement {
    var executor: Executor
    var imageCapture: ImageCapture? = null

    // TODO: This is probably not the best default executor for this task. Just trying to get things up and running
    @RequiresApi(Build.VERSION_CODES.N)
    constructor(executor: Executor = Executors.newWorkStealingPool(4)) {
        this.executor = executor
    }

    fun startCamera(liveCycleOwner: LifecycleOwner,
                    textureView: TextureView,
                    context: Context) {
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // In our analysis, we care more about the latest image than
            // analyzing *every* image
            setImageReaderMode(
                ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        FirebaseApp.initializeApp(context)
        val analyzerUseCase = ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, FirebaseAnalyzer())
        }

        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ratio and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        imageCapture = ImageCapture(imageCaptureConfig)

        val previewConfig = PreviewConfig.Builder().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setTargetResolution(Size(640, 480))
            }
        }.build()

        val preview = Preview(previewConfig)
        preview.setOnPreviewOutputUpdateListener {
            val parent = textureView.parent as ViewGroup
            parent.removeView(textureView)
            parent.addView(textureView, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textureView.surfaceTexture = it.surfaceTexture
            }
            updateTransform(textureView)
        }

        CameraX.bindToLifecycle(liveCycleOwner, preview, imageCapture, analyzerUseCase)
    }

    fun capturePicture(dir: File,
                       viewFinder: View,
                       context: Context
    ) {
        val file = File(dir,
            "${System.currentTimeMillis()}.jpg")

        imageCapture?.takePicture(file, executor,
            object : ImageCapture.OnImageSavedListener {
                override fun onError(
                    imageCaptureError: ImageCapture.ImageCaptureError,
                    message: String,
                    exc: Throwable?
                ) {
                    val msg = "Photo capture failed: $message"
                    Log.e("CameraXApp", msg, exc)
                    viewFinder.post {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onImageSaved(file: File) {
                    val msg = "Photo capture succeeded: ${file.absolutePath}"
                    Log.d("CameraXApp", msg)
                    viewFinder.post {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    fun updateTransform(textureView: TextureView) {
        val matrix = Matrix()
        val centerX = textureView.width / 2f
        val centerY = textureView.height / 2f

        val rotationDegrees = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            when(textureView.display.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> return
            }
        } else {
            TODO("VERSION.SDK_INT < JELLY_BEAN_MR1")
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        textureView.setTransform(matrix)
    }
}