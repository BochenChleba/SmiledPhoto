package com.example.smiledphoto.ui.gallery

import android.graphics.Bitmap
import java.text.SimpleDateFormat
import java.util.*

data class GalleryItem(
    val path: String,
    val thumbnail: Bitmap
) {
    companion object {
        private const val datePattern = "dd-MM-yyy HH:mm:ss"
    }
    private val dateFormatter = SimpleDateFormat(datePattern, Locale.getDefault())

    val fileName: String
        get() = path.let {
            it.substring(it.lastIndexOf('/') + 1)
        }

    val date: String
        get() = run {
            val ts = path.let {
                it.substring(it.lastIndexOf('_') + 1, it.lastIndexOf('.'))
            }.toLong()
            dateFormatter.format(Date(ts))
        }
}
