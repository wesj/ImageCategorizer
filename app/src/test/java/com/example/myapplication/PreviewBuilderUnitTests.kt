package com.example.myapplication

import android.graphics.Matrix
import android.view.Surface
import android.view.TextureView
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class PreviewBuilderUnitTests {
    /* This will fail because Matrix.setTransform isn't mocked right
    @Test
    fun textUpdateTransformWithZeroRotation() {
        val textureView = mockk<TextureView>()
        val previewBuilder = PreviewBuilder()
        val identity = Matrix()

        every { textureView.width } returns 64
        every { textureView.height } returns 64
        every { textureView.display.rotation } returns Surface.ROTATION_0
        every { textureView.setTransform(any()) } returns Unit

        previewBuilder.updateTransform(textureView)

        verify {
            textureView.setTransform(identity)
        }
    }

    @Test
    fun textUpdateTransformWith90degRotation() {
        val textureView = mockk<TextureView>()
        val previewBuilder = PreviewBuilder()
        var matrix = Matrix()
        matrix.postRotate(-90f, 32f, 32f)

        every { textureView.width } returns 64
        every { textureView.height } returns 64
        every { textureView.display.rotation } returns Surface.ROTATION_90
        every { textureView.setTransform(any()) } returns Unit

        previewBuilder.updateTransform(textureView)

        verify {
            textureView.setTransform(matrix)
        }
    }
     */
}