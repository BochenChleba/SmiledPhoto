package com.example.smiledphoto.ui.gallery.rv

import androidx.recyclerview.widget.RecyclerView
import com.example.smiledphoto.databinding.RecyclerItemGalleryBinding
import com.example.smiledphoto.ui.gallery.GalleryItem

class GalleryRecyclerViewHolder(
    private val binding: RecyclerItemGalleryBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(dto: GalleryItem) {
        binding.galleryItem = dto
        binding.photoThumbnailImageView.setImageBitmap(dto.thumbnail)
        binding.executePendingBindings()
    }
}
