package com.github.bochenchleba.smiledphoto.data.preferences

import com.github.bochenchleba.smiledphoto.data.enumeration.CameraTypeEnum
import com.github.bochenchleba.smiledphoto.data.enumeration.QualityEnum

interface Preferences {
    var quality: QualityEnum
    var smilingProbabilityThreshold: Float
    var cameraType: CameraTypeEnum
}
