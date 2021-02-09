package com.example.smiledphoto.data.constants

import android.Manifest

object Constants {
    const val TAG = "Smiled Photo"
    const val REQUEST_CODE_PERMISSIONS = 1
    const val DEFAULT_SMILING_PROBABILITY_THRESHOLD = 0.65f
    const val PHOTO_FILE_PREFIX = "SmiledFace_"
    const val PHOTO_FILE_EXTENSION = ".jpg"
    const val BUNDLE_PHOTO_PATH = "1"
    const val BUNDLE_GALLERY_PATH = "2"
    const val PREFERENCES_NAME = "SMILED_PHOTO_PREFERENCES"
    const val IMAGE_MIME_TYPE = "image/*"
    const val FILE_AUTHORITY = "com.example.smiledphoto"
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}
