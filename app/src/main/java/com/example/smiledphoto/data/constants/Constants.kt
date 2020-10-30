package com.example.smiledphoto.data.constants

import android.Manifest

object Constants {
    const val TAG = "Smiled Photo"
    const val REQUEST_CODE_PERMISSIONS = 1
    const val SMILING_PROBABILITY_THRESHOLD = 0.40f
    const val PHOTO_FILE_PREFIX = "SmiledFace_"
    const val PHOTO_FILE_EXTENSION = ".jpg"
    const val BUNDLE_PHOTO_PATH = "1"
    const val PREFERENCES_NAME = "SMILED_PHOTO_PREFERENCES"
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
}
