package com.example.smiledphoto.ui.photo_dialog

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider.getUriForFile
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
import java.io.File

class PhotoDialog : DialogFragment(), KoinComponent {

    companion object {
        fun newInstance(photoPath: String) = PhotoDialog().putExtras {
            putString(Constants.BUNDLE_PHOTO_PATH, photoPath)
        }
    }

    private val viewModel: PhotoDialogViewModel by viewModel()
    val photoPath: String by lazy {
        requireArguments().getString(Constants.BUNDLE_PHOTO_PATH)!!
    }

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
        observePhotoBitmap()
        observePhotoDeletion()
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

    private fun observePhotoDeletion() {
        viewModel.photoDeletedLiveData.observe(viewLifecycleOwner, Observer { deletedSuccessfully ->
            deletedSuccessfully ?: return@Observer
            if (deletedSuccessfully) {
                toast(R.string.photo_deleted_toast)
                dismiss()
            } else {
                toast(R.string.photo_delete_error_toast)
            }
        })
    }

    fun shareImage() {
        val uri = getUriForFile(requireContext(), Constants.FILE_AUTHORITY, File(photoPath))
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = Constants.IMAGE_MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_image_chooser_title)))
    }
}
