package com.example.myapplication

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysisConfig
import androidx.camera.core.UseCase
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import java.util.concurrent.Executor

class AnalyzerBuilder(private val callback: Callback) {
    enum class Type {
        Unknown { },
        HomeGood { },
        FashionGood { },
        Food { },
        Place { },
        Plant { };

        companion object {
            fun from(category: Int): Type {
                return when(category) {
                    FirebaseVisionObject.CATEGORY_FASHION_GOOD -> FashionGood
                    FirebaseVisionObject.CATEGORY_FOOD -> Food
                    FirebaseVisionObject.CATEGORY_HOME_GOOD -> HomeGood
                    FirebaseVisionObject.CATEGORY_PLACE -> Place
                    FirebaseVisionObject.CATEGORY_PLANT -> Plant
                    else -> Unknown
                }
            }
        }
    }

    interface Callback {
        fun onItemFound(item: Type)
        fun nothingFound()
    }

    fun buildUseCase(executor: Executor): UseCase {
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        return ImageAnalysis(analyzerConfig).apply {
            setAnalyzer(executor, FirebaseAnalyzer(callback))
        }

    }
}
