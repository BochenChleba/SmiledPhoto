package com.github.bochenchleba.smiledphoto.ui.photo_dialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.bochenchleba.smiledphoto.data.constants.Constants
import com.github.bochenchleba.smiledphoto.util.BitmapHelper
import kotlinx.coroutines.*
import java.io.File

class PhotoDialogViewModel : ViewModel() {
    val photoBitmapLiveData by lazy { MutableLiveData<Bitmap>() }
    val photoDeletedLiveData by lazy { MutableLiveData<Boolean>() }
    var loadPhotoJob: Job? = null

    fun getPhotoBitmapByPath(filePath: String) {
        loadPhotoJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeFile(filePath)
                ensureActive()
                val rotatedBitmap = BitmapHelper.rotate(filePath, bitmap)
                photoBitmapLiveData.postValue(rotatedBitmap)
            } catch (ex: Throwable) {
                if (ex is CancellationException) {
                    Log.i(Constants.TAG, "Loading photo has been canceled", ex)
                } else {
                    Log.e(Constants.TAG, "Error while loading bitmap from file: $filePath", ex)
                    photoBitmapLiveData.postValue(null)
                }
            }
        }
    }

    fun deleteImage(filePath: String) {
        loadPhotoJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                File(filePath).delete()
                photoDeletedLiveData.postValue(true)
            } catch (ex: Throwable) {
                Log.e(Constants.TAG, "Error while deleting file: $filePath", ex)
                photoDeletedLiveData.postValue(false)
            }
        }
    }
}
