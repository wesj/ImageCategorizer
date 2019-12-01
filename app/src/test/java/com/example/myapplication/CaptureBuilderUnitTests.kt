package com.example.myapplication

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.io.File
import java.util.concurrent.Executor

class CaptureBuilderUnitTests {
    /* This will throw because creating a CaptureUseCase depends on CameraX being initialized
    @Test
    fun testCapturePicture() {
        val callback = mockk<CaptureBuilder.Callback>()
        val file = File("")
        val executor = mockk<Executor>()
        val builder = CaptureBuilder(callback)

        builder.capturePicture(file, executor)

        verify {
            callback.errorSavingImage(any())
        }
    }
     */
}