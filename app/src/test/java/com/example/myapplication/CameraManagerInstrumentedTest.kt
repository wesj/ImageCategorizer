package com.example.myapplication

import android.view.TextureView
import androidx.camera.core.UseCase
import androidx.lifecycle.LifecycleOwner
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.util.concurrent.Executor

@RunWith(JUnit4::class)
class CameraManagerInstrumentedTest() {
    @Test
    fun testStartCamera() {
        val systemApi = mockk<CameraManagement.SystemApi>()
        val captureBuilder = mockk<CaptureBuilder>()
        val captureCallback = mockk<CaptureBuilder.Callback>()
        val previewBuilder = mockk<PreviewBuilder>()
        val analyzerManager = mockk<AnalyzerBuilder>()
        val executor = mockk<Executor>()
        val manager = CameraManagement(
            captureBuilder,
            previewBuilder,
            analyzerManager,
            executor,
            systemApi)

        val liveCycleOwner = mockk<LifecycleOwner>()
        val textureView = mockk<TextureView>()
        val useCase = mockk<UseCase>()

        every { previewBuilder.buildUseCase(textureView) } returns useCase
        every { analyzerManager.buildUseCase(executor) } returns useCase
        every { captureBuilder.buildUseCase() } returns useCase
        every { systemApi.bindToLifecycle(liveCycleOwner, useCase, useCase, useCase) } returns Unit

        manager.startCamera(liveCycleOwner, textureView)

        verify {
            previewBuilder.buildUseCase(textureView)
            analyzerManager.buildUseCase(executor)
            captureBuilder.buildUseCase()
            systemApi.bindToLifecycle(liveCycleOwner, useCase, useCase, useCase)
        }
    }

    @Test
    fun testUpdateTransform() {
        val systemApi = mockk<CameraManagement.SystemApi>()
        val captureBuilder = mockk<CaptureBuilder>()
        val previewBuilder = mockk<PreviewBuilder>()
        val analyzerManager = mockk<AnalyzerBuilder>()
        val executor = mockk<Executor>()
        val manager = CameraManagement(captureBuilder,
            previewBuilder,
            analyzerManager,
            executor,
            systemApi)

        val textureView = mockk<TextureView>()

        every { previewBuilder.updateTransform(textureView) } returns Unit

        manager.updateTransform(textureView)

        verify {
            previewBuilder.updateTransform(textureView)
        }
    }
}
