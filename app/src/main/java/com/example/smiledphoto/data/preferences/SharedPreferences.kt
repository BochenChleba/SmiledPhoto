package com.example.smiledphoto.data.preferences

import android.content.Context
import com.example.smiledphoto.data.constants.Constants
import com.example.smiledphoto.data.enumeration.CameraTypeEnum
import com.example.smiledphoto.data.enumeration.QualityEnum

class SharedPreferences(context: Context) :
    Preferences {
    companion object {
        private const val KEY_QUALITY = "1"
        private const val KEY_SMILING_PROBABILITY_THRESHOLD = "2"
        private const val KEY_CAMERA_TYPE = "3"
        private val DEFAULT_QUALITY = QualityEnum.HIGH.ordinal
        private val DEFAULT_CAMERA_TYPE = CameraTypeEnum.BACK.ordinal
    }

    private val sharedPreferencesInstance =
        context.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)

    override var quality: QualityEnum
        get() {
            val ordinal = sharedPreferencesInstance.getInt(
                KEY_QUALITY,
                DEFAULT_QUALITY
            )
            return QualityEnum.values()[ordinal]
        }
        set(value) {
            sharedPreferencesInstance.edit().apply {
                putInt(KEY_QUALITY, value.ordinal)
                apply()
            }
        }

    override var smilingProbabilityThreshold: Float
        get() = sharedPreferencesInstance.getFloat(
            KEY_SMILING_PROBABILITY_THRESHOLD,
            Constants.DEFAULT_SMILING_PROBABILITY_THRESHOLD
        )
        set(value) {
            sharedPreferencesInstance.edit().apply {
                putFloat(KEY_SMILING_PROBABILITY_THRESHOLD, value)
                apply()
            }
        }

    override var cameraType: CameraTypeEnum
        get() {
            val ordinal = sharedPreferencesInstance.getInt(
                KEY_CAMERA_TYPE,
                DEFAULT_CAMERA_TYPE
            )
            return CameraTypeEnum.values()[ordinal]
        }
        set(value) {
            sharedPreferencesInstance.edit().apply {
                putInt(KEY_CAMERA_TYPE, value.ordinal)
                apply()
            }
        }
}
