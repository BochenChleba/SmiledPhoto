package com.example.smiledphoto.ui.photo_dialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smiledphoto.data.constants.Constants
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
                val rotatedBitmap = rotate(filePath, bitmap)
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

    private fun rotate(filePath: String, bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
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
