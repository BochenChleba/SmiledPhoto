package com.example.smiledphoto

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.smiledphoto.databinding.DialogPhotoBinding
import kotlinx.android.synthetic.main.dialog_photo.*
import org.jetbrains.anko.support.v4.toast

class PhotoDialog : DialogFragment() {

    companion object {
        fun newInstance(photoPath: String) = PhotoDialog().putExtras {
            putString(Constants.BUNDLE_PHOTO_PATH, photoPath)
        }
    }

    private val viewModel: PhotoDialogViewModel by lazy {
        ViewModelProvider(this).get(PhotoDialogViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil
            .inflate<DialogPhotoBinding>(inflater, R.layout.dialog_photo, container, false)
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
            if (bitmap == null) {
                toast(R.string.photo_load_error_toast)
                dismiss()
            } else {
                photoPreviewImageView.setImageBitmap(bitmap)
            }
        })
    }
}
