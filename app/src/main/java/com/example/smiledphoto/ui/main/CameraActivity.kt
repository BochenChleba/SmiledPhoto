package com.example.smiledphoto.ui.main

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.smiledphoto.R
import com.example.smiledphoto.data.constants.Constants
import com.example.smiledphoto.data.enumeration.CameraTypeEnum
import com.example.smiledphoto.data.enumeration.QualityEnum
import com.example.smiledphoto.extension.showDialog
import com.example.smiledphoto.ui.photo_dialog.PhotoDialog
import com.example.smiledphoto.ui.settings_dialog.SettingsDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.File
import java.util.concurrent.Executors

class CameraActivity : MainActivity() {
    private val processingExecutor = Executors.newCachedThreadPool()
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private val imageAnalyzer = ImageAnalyzer { savePhoto() }
    private val previewUseCase: Preview by lazy {
        Preview.Builder()
            .build()
            .also { it.setSurfaceProvider(cameraView.surfaceProvider) }
    }
    private val imageAnalysisUseCase: ImageAnalysis by lazy {
        ImageAnalysis.Builder()
            .setTargetRotation(cameraView.display.rotation)
            .build()
            .also { it.setAnalyzer(processingExecutor, imageAnalyzer) }
    }
    private var imageCaptureUseCase: ImageCapture =
        buildImageCaptureUseCase(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)

    private fun buildImageCaptureUseCase(@ImageCapture.CaptureMode captureMode: Int) =
        ImageCapture.Builder()
            .setCaptureMode(captureMode)
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .setIoExecutor(processingExecutor)
            .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isImageAnalysisActive.observe(this, Observer { isActive ->
            imageAnalyzer.active = isActive
        })
        viewModel.savedPhotoPath.observe(this, Observer { path ->
            if (path == null) {
                toast(R.string.take_photo_error_toast)
            } else {
                showDialog(PhotoDialog.newInstance(path))
            }
        })
        imageAnalyzer.probabilityLiveData.observe(this, Observer { probability ->
            probabilityTextView.text = probability?.toString()
        })
    }

    override fun startCamera() {
        getCameraProviderAsync { cameraProvider ->
            val useCases = arrayOf(previewUseCase, imageAnalysisUseCase, imageCaptureUseCase)
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, *useCases)
        }
    }

    private fun savePhoto() {
        viewModel.isImageAnalysisActive.value = false
        val outputFile = viewModel.createOutputFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
        if (viewModel.preferences.quality == QualityEnum.LOW) {
            takeLowQualityPhoto(outputFile)
        } else {
            takeHighQualityPhoto(outputFile)
        }
    }

    private fun takeLowQualityPhoto(outputFile: File) {
        val bitmap = cameraView.bitmap
        viewModel.saveBitmapToFile(outputFile.path, bitmap)
    }

    private fun takeHighQualityPhoto(outputFile: File) {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        imageCaptureUseCase.takePicture(
            outputFileOptions,
            processingExecutor,
            viewModel.getImageSavedCallback(outputFile.path)
        )
    }

    private fun getCameraProviderAsync(block: (ProcessCameraProvider) -> Unit) {
        val cameraFuture = ProcessCameraProvider.getInstance(this)
        cameraFuture.addListener(Runnable {
            val cameraProvider = cameraFuture.get()
            try {
                block(cameraProvider)
            } catch (ex: Throwable) {
                Log.e(Constants.TAG, "Camera provider operation failed", ex)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is SettingsDialog -> {
                fragment.onSettingsChanged = { applySettings() }
            }
        }
    }

    private fun applySettings() {
        when (viewModel.preferences.quality) {
            QualityEnum.MEDIUM -> {
                imageCaptureUseCase = buildImageCaptureUseCase(
                    ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            }
            QualityEnum.HIGH -> {
                imageCaptureUseCase = buildImageCaptureUseCase(
                    ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            }
            else -> {}
        }
        imageAnalyzer.smilingProbabilityThreshold = viewModel.preferences.smilingProbabilityThreshold
        cameraSelector = when (viewModel.preferences.cameraType) {
            CameraTypeEnum.BACK -> {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            CameraTypeEnum.FRONT -> {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
        }
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        processingExecutor.shutdown()
    }
}
