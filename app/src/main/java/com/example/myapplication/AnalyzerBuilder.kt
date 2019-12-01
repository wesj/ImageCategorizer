package com.example.myapplication

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysisConfig
import androidx.camera.core.UseCase
import java.util.concurrent.Executor

class AnalyzerBuilder {
    fun buildUseCase(executor: Executor): UseCase {
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        return ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, FirebaseAnalyzer())
        }

    }
}
