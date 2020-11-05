package com.example.smiledphoto.ui.main

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smiledphoto.data.constants.Constants
import com.example.smiledphoto.data.preferences.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class MainViewModel(
    val preferences: Preferences
) : ViewModel() {
    val isImageAnalysisActive: MutableLiveData<Boolean> = MutableLiveData(false)
    val savedPhotoPath: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun toggleIsImageAnalysisActive() {
        isImageAnalysisActive.value = !isImageAnalysisActive.value!!
    }

    fun createOutputFile(externalFileDir: File) = File(
        externalFileDir.path.toString() +
                File.separatorChar +
                Constants.PHOTO_FILE_PREFIX +
                System.currentTimeMillis() +
                Constants.PHOTO_FILE_EXTENSION
    )

    fun saveBitmapToFile(path: String, bitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FileOutputStream(path).use { outputStream ->
                    bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                Log.d(Constants.TAG, "Photo saved")
                savedPhotoPath.postValue(path)
            } catch (ex: Exception) {
                Log.e(Constants.TAG, "Photo take exception", ex)
                savedPhotoPath.postValue(null)
            }
        }
    }

    fun getImageSavedCallback(path: String) = object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            savedPhotoPath.postValue(path)
        }
        override fun onError(exception: ImageCaptureException) {
            savedPhotoPath.postValue(null)
        }
    }
}
