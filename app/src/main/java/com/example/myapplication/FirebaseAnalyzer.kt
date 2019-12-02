package com.example.myapplication

import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.objects.FirebaseVisionObject
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions.Builder
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions.STREAM_MODE


class FirebaseAnalyzer(private val callback: AnalyzerBuilder.Callback) : ImageAnalysis.Analyzer {
    val options = Builder()
        .setDetectorMode(STREAM_MODE)
        .enableClassification()  // Optional
        .build()
    val objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)
    // TODO: Need to be more careful with concurrency here
    var processing = false

    private fun degreesToFirebaseRotation(degrees: Int): Int {
        return when (degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw IllegalArgumentException(
                "Rotation must be 0, 90, 180, or 270."
            )
        }
    }

    override fun analyze(imageProxy: ImageProxy?, degrees: Int) {
        if (processing) {
            // Log.d("TOY", "Busy")
            return
        }

        if (imageProxy == null || imageProxy.image == null) {
            return
        }

        val mediaImage: Image? = imageProxy.image
        if (mediaImage != null) {
            processing = true
            val rotation = degreesToFirebaseRotation(degrees)
            val image = FirebaseVisionImage.fromMediaImage(mediaImage!!, rotation)
            objectDetector.processImage(image)
                .addOnSuccessListener { detectedObjects ->
                    if (detectedObjects.size == 0) {
                        callback.nothingFound()
                    }

                    for (obj in detectedObjects) {
                        val category = obj.classificationCategory
                        // Log.d("TOY", "Found "  + category)

                        val type = AnalyzerBuilder.Type.from(category)
                        callback.onItemFound(type)
                    }
                    processing = false
                }
                .addOnFailureListener { e ->
                    // Log.d("TOY", "Error " + e)
                    processing = false
                }
        }
    }

}