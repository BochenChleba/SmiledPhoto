package com.github.bochenchleba.smiledphoto.ui.settings_dialog

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.github.bochenchleba.smiledphoto.R
import com.github.bochenchleba.smiledphoto.data.enumeration.CameraTypeEnum
import com.github.bochenchleba.smiledphoto.data.enumeration.QualityEnum
import com.github.bochenchleba.smiledphoto.databinding.DialogSettingsBinding
import com.github.bochenchleba.smiledphoto.extension.setMatchParentWidth
import com.github.bochenchleba.smiledphoto.extension.setProgressChangedListener
import kotlinx.android.synthetic.main.dialog_settings.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

class SettingsDialog : DialogFragment(), KoinComponent {

    companion object {
        fun newInstance() = SettingsDialog()
    }

    lateinit var onSettingsChanged: () -> Unit
    private val viewModel: SettingsDialogViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil
            .inflate<DialogSettingsBinding>(inflater,
                R.layout.dialog_settings, container, false)
            .also {
                it.lifecycleOwner = this
                it.dialog = this
                it.viewModel = viewModel
            }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setMatchParentWidth()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        probabilityThresholdSeekBar.setProgressChangedListener { progress ->
            probabilityThresholdValueTextView.text = (progress.toFloat() / 100).toString()
        }
    }

    fun saveSettings() {
        viewModel.savePreferences(
            getSelectedQuality(),
            getSelectedSmilingProbability(),
            getSelectedCameraType()
        )
        onSettingsChanged()
        dismiss()
    }

    private fun getSelectedQuality() =
        when (qualityRadioGroup.checkedRadioButtonId) {
            qualityHighRadioButton.id ->
                QualityEnum.HIGH
            qualityMediumRadioButton.id ->
                QualityEnum.MEDIUM
            qualityLowRadioButton.id ->
                QualityEnum.LOW
            else ->
                throw IllegalStateException("All radio buttons must refer to Quality Enum value")
        }

    private fun getSelectedSmilingProbability() =
        probabilityThresholdSeekBar.progress.toFloat() / 100

    private fun getSelectedCameraType() =
        when (cameraTypeRadioGroup.checkedRadioButtonId) {
            cameraTypeBackRadioButton.id ->
                CameraTypeEnum.BACK
            cameraTypeFrontRadioButton.id ->
                CameraTypeEnum.FRONT
            else ->
                throw IllegalStateException("All radio buttons must refer to Camera Type Enum value")
        }
}
