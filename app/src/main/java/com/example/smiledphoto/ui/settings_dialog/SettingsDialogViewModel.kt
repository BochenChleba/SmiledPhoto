package com.example.smiledphoto.ui.settings_dialog

import androidx.lifecycle.ViewModel
import com.example.smiledphoto.data.enumeration.CameraTypeEnum
import com.example.smiledphoto.data.enumeration.QualityEnum
import com.example.smiledphoto.data.preferences.Preferences

class SettingsDialogViewModel(
    val preferences: Preferences
) : ViewModel() {

    fun savePreferences(
        quality: QualityEnum,
        smilingProbability: Float,
        cameraType: CameraTypeEnum
    ) {
        preferences.quality = quality
        preferences.smilingProbabilityThreshold = smilingProbability
        preferences.cameraType = cameraType
    }
}
