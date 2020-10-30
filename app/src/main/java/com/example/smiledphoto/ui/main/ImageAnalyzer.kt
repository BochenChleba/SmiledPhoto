package com.example.smiledphoto.ui.main

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.example.smiledphoto.data.constants.Constants
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class ImageAnalyzer(
    private val onSmiledFaceDetected: () -> Unit
) : ImageAnalysis.Analyzer {

    val probabilityLiveData by lazy {
        MutableLiveData<Float>(0f)
    }

    private val detectorOptions = FaceDetectorOptions.Builder()
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    private val detector = FaceDetection.getClient(detectorOptions)
    var active = true

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    if (!active) {
                        return@addOnSuccessListener
                    }
                    val allFacesSmiled = faces.isNotEmpty() && faces.all {
                        it.smilingProbability ?: 0f > Constants.SMILING_PROBABILITY_THRESHOLD
                    }
                    if (allFacesSmiled) {
                        active = false
                        onSmiledFaceDetected()
                    }
                    if (faces.isNotEmpty()) {
                        probabilityLiveData.postValue(faces[0].smilingProbability)
                    } else {
                        probabilityLiveData.postValue(0f)
                    }
                }
                .addOnFailureListener { ex ->
                    Log.e(Constants.TAG, "Face detection failed", ex)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}
