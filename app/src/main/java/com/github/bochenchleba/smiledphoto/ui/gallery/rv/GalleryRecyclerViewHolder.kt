package com.github.bochenchleba.smiledphoto.ui.gallery.rv

import androidx.recyclerview.widget.RecyclerView
import com.github.bochenchleba.smiledphoto.databinding.RecyclerItemGalleryBinding
import com.github.bochenchleba.smiledphoto.extension.gone
import com.github.bochenchleba.smiledphoto.extension.visible
import com.github.bochenchleba.smiledphoto.ui.gallery.GalleryItem

class GalleryRecyclerViewHolder(
    val binding: RecyclerItemGalleryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(dto: GalleryItem, listener: GalleryRecyclerAdapter.GalleryItemActions) {
        binding.galleryItem = dto
        binding.photoThumbnailImageView.setImageBitmap(dto.thumbnail)
        binding.showTextView.setOnClickListener {
            listener.show(dto.path)
        }
        binding.shareTextView.setOnClickListener {
            listener.share(dto.path)
        }
        binding.deleteTextView.setOnClickListener {
            listener.delete(dto.path)
        }
        binding.executePendingBindings()
    }

    fun setItemClickListener(listener: () -> Unit) {
        binding.root.setOnClickListener { listener() }
    }

    fun showGalleryItemActions() {
        binding.showTextView.visible()
        binding.shareTextView.visible()
        binding.deleteTextView.visible()
    }

    fun hideGalleryItemActions() {
        binding.showTextView.gone()
        binding.shareTextView.gone()
        binding.deleteTextView.gone()
    }
}
