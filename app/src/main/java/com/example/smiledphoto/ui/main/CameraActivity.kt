package com.example.smiledphoto.ui.main

import android.os.Environment
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.smiledphoto.data.constants.Constants
import com.example.smiledphoto.extension.showDialog
import com.example.smiledphoto.ui.photo_dialog.PhotoDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.io.File
import java.util.concurrent.Executors

class CameraActivity : MainActivity() {
    private val processingExecutor = Executors.newCachedThreadPool()
    private val imageAnalyzer =
        ImageAnalyzer { savePhoto() }
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
    private val imageCaptureUseCase: ImageCapture by lazy {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .setIoExecutor(processingExecutor)
            .build()
    }

    override fun startCamera() {
        pocButton.setOnClickListener {
            imageAnalyzer.active = true
        }
        imageAnalyzer.probabilityLiveData.observe(this, Observer { probability ->
            probabilityTextView.text = probability?.toString()
        })
        getCameraProviderAsync { cameraProvider ->
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            val useCases = arrayOf(previewUseCase, imageAnalysisUseCase, imageCaptureUseCase)
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this, cameraSelector, *useCases)
        }
    }

    private fun savePhoto() {
        //todo save from cameraView.bitmap
        val outputFile = createOutputFile()
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        imageCaptureUseCase.takePicture(outputFileOptions, processingExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(Constants.TAG, "Photo saved")
                    showDialog(
                        PhotoDialog.newInstance(outputFile.path)
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(Constants.TAG, "Photo take exception", exception)
                    runOnUiThread {
                        toast("Cannot take photo")
                    }
                }
            })
    }

    private fun createOutputFile() = File(
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.path.toString() +
                File.separatorChar +
                Constants.PHOTO_FILE_PREFIX +
                System.currentTimeMillis() +
                Constants.PHOTO_FILE_EXTENSION
    )

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

    override fun onDestroy() {
        super.onDestroy()
        processingExecutor.shutdown()
    }

}
