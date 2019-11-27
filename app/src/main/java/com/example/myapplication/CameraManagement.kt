package com.example.myapplication

import android.graphics.Matrix
import android.os.Build
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.PreviewConfig
import androidx.lifecycle.LifecycleOwner

class CameraManagement {
    fun startCamera(liveCycleOwner: LifecycleOwner, textureView: TextureView) {
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

        CameraX.bindToLifecycle(liveCycleOwner, preview)
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