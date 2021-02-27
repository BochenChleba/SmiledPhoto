package com.github.bochenchleba.smiledphoto.ui.settings_dialog

import androidx.lifecycle.ViewModel
import com.github.bochenchleba.smiledphoto.data.enumeration.CameraTypeEnum
import com.github.bochenchleba.smiledphoto.data.enumeration.QualityEnum
import com.github.bochenchleba.smiledphoto.data.preferences.Preferences

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
