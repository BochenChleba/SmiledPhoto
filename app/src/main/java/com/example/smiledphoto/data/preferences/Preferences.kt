package com.example.smiledphoto.data.preferences

import com.example.smiledphoto.data.enumeration.CameraTypeEnum
import com.example.smiledphoto.data.enumeration.QualityEnum

interface Preferences {
    var quality: QualityEnum
    var smilingProbabilityThreshold: Float
    var cameraType: CameraTypeEnum
}
