package com.example.smiledphoto.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.FileProvider.getUriForFile
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.smiledphoto.R
import com.example.smiledphoto.data.constants.Constants
import com.example.smiledphoto.databinding.DialogGalleryBinding
import com.example.smiledphoto.extension.gone
import com.example.smiledphoto.extension.putExtras
import com.example.smiledphoto.extension.setListeners
import com.example.smiledphoto.extension.setMatchParentWidth
import com.example.smiledphoto.ui.gallery.rv.GalleryRecyclerAdapter
import kotlinx.android.synthetic.main.dialog_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import java.io.File

class GalleryDialog : DialogFragment(), KoinComponent {

    companion object {
        fun newInstance(galleryPath: String) = GalleryDialog().putExtras {
            putString(Constants.BUNDLE_GALLERY_PATH, galleryPath)
        }
    }

    private val viewModel: GalleryDialogViewModel by viewModel()
    private val recyclerAdapter = GalleryRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil
            .inflate<DialogGalleryBinding>(
                inflater, R.layout.dialog_gallery, container, false)
            .apply {
                lifecycleOwner = this@GalleryDialog
                dialog = this@GalleryDialog
                viewModel = this@GalleryDialog.viewModel
            }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setMatchParentWidth()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeProgressLiveData()
        observeGalleryLiveData()
        loadGallery()
    }

    private fun observeProgressLiveData() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading == false) {
                animateProgressBarOnLoadFinish()
            }
        })
    }

    private fun animateProgressBarOnLoadFinish() {
        val enlargeAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.enlarge)
        val disappearAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.disappear)
        enlargeAnim.setListeners(
            onEnd = {
                progress.startAnimation(disappearAnim)
            }
        )
        disappearAnim.setListeners(
            onEnd = {
                progress.gone()
            }
        )
        progress.startAnimation(enlargeAnim)
    }

    private fun observeGalleryLiveData() {
        viewModel.galleryLiveData.observe(viewLifecycleOwner, Observer { galleryItems ->
            recyclerAdapter.append(galleryItems)
        })
    }

    private fun loadGallery() {
        galleryRecyclerView.adapter = recyclerAdapter
        val galleryPath = requireArguments().getString(Constants.BUNDLE_GALLERY_PATH)!!
        viewModel.loadGallery(galleryPath)
    }

    fun shareImage(path: String) {
        val uri = getUriForFile(requireContext(), Constants.FILE_AUTHORITY, File(path))
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = Constants.IMAGE_MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.share_image_chooser_title)))
    }
}
