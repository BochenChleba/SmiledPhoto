package com.example.smiledphoto.ui.gallery

import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smiledphoto.data.constants.Constants
import com.example.smiledphoto.extension.postIncrement
import com.example.smiledphoto.util.BitmapHelper
import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

class GalleryDialogViewModel : ViewModel() {
    val galleryLiveData by lazy { MutableLiveData<MutableList<GalleryItem>>() }
    val imageDeletedLiveData by lazy { MutableLiveData<Int>() }
    val isLoading by lazy { MutableLiveData<Boolean>(null) }
    val progressLiveData by lazy { MutableLiveData(AtomicInteger(0)) }
    val filesCountLiveData by lazy { MutableLiveData(0) }

    fun loadGallery(galleryPath: String) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                File(galleryPath)
                    .listFiles()!!.toList()
                    .also { filesCountLiveData.postValue(it.size) }
                    .chunked(Constants.GALLERY_BATCH_SIZE)
                    .forEach { loadPartOfGallery(it) }
            } catch (ex: Throwable) {
                galleryLiveData.postValue(null)
            } finally {
                isLoading.postValue(false)
            }
        }
    }

    private suspend fun loadPartOfGallery(files: List<File>) = withContext(Dispatchers.IO) {
        val galleryItems = mutableListOf<GalleryItem>()
        files.map { file ->
            async {
                val path = file.path
                val bitmap = BitmapFactory.decodeFile(path)
                val thumbnailBitmap = BitmapHelper.resize(bitmap)
                val rotatedBitmap = BitmapHelper.rotate(path, thumbnailBitmap)
                galleryItems.add(GalleryItem(path, rotatedBitmap))
                progressLiveData.postIncrement()
            }
        }.awaitAll()
        galleryLiveData.postValue(galleryItems)
    }

    fun deleteImage(index: Int) {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val path = galleryLiveData.value!![index].path
                galleryLiveData.value!!.removeAt(index)
                File(path).delete()
                imageDeletedLiveData.postValue(index)
            } catch (ex: Throwable) {
                Log.e(Constants.TAG, "Error while deleting file at index: $index", ex)
                imageDeletedLiveData.postValue(null)
            } finally {
                isLoading.postValue(false)
            }
        }
    }
}
