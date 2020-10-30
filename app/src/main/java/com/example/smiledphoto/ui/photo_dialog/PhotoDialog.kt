package com.example.smiledphoto.ui.photo_dialog

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
import com.example.smiledphoto.extension.gone
import com.example.smiledphoto.extension.putExtras
import kotlinx.android.synthetic.main.dialog_photo.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent

class PhotoDialog : DialogFragment(), KoinComponent {

    companion object {
        fun newInstance(photoPath: String) = PhotoDialog().putExtras {
            putString(Constants.BUNDLE_PHOTO_PATH, photoPath)
        }
    }

    private val viewModel: PhotoDialogViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil
            .inflate<DialogPhotoBinding>(inflater,
                R.layout.dialog_photo, container, false)
            .apply {
                lifecycleOwner = this@PhotoDialog
                dialog = this@PhotoDialog
                viewModel = this@PhotoDialog.viewModel
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
        val photoPath = requireArguments().getString(Constants.BUNDLE_PHOTO_PATH)
        observePhotoBitmap()
        viewModel.getPhotoBitmapByPath(photoPath)
    }

    private fun observePhotoBitmap() {
        viewModel.photoBitmapLiveData.observe(viewLifecycleOwner, Observer { bitmap ->
            progressBar.gone()
            if (bitmap == null) {
                toast(R.string.photo_load_error_toast)
                dismiss()
            } else {
                photoPreviewImageView.setImageBitmap(bitmap)
            }
        })
    }
}
