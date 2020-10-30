package com.example.smiledphoto.ui.settings_dialog

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.smiledphoto.*
import com.example.smiledphoto.data.constants.Constants
import com.example.smiledphoto.databinding.DialogPhotoBinding
import com.example.smiledphoto.databinding.DialogSettingsBinding
import com.example.smiledphoto.extension.gone
import com.example.smiledphoto.extension.putExtras
import kotlinx.android.synthetic.main.dialog_photo.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

class SettingsDialog : DialogFragment(), KoinComponent {

    companion object {
        fun newInstance() = SettingsDialog()
    }

    private val viewModel: SettingsDialogViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil
            .inflate<DialogSettingsBinding>(inflater,
                R.layout.dialog_settings, container, false)
            .apply {
                lifecycleOwner = this@SettingsDialog
                dialog = this@SettingsDialog
                viewModel = this@SettingsDialog.viewModel
            }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setDialogSize()
    }

    private fun setDialogSize() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        } else {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
