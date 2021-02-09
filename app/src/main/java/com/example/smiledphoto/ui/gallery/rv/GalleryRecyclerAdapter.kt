package com.example.smiledphoto.ui.gallery.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smiledphoto.databinding.RecyclerItemGalleryBinding
import com.example.smiledphoto.extension.gone
import com.example.smiledphoto.extension.visible
import com.example.smiledphoto.ui.gallery.GalleryItem
import kotlinx.coroutines.CoroutineScope

class GalleryRecyclerAdapter(
    private val listener: GalleryItemActions
) : RecyclerView.Adapter<GalleryRecyclerViewHolder>() {

    private val items = mutableListOf<GalleryItem>()
    private var selectedItemPosition: Int? = null

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : GalleryRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerItemGalleryBinding.inflate(inflater, parent, false)
        return GalleryRecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryRecyclerViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
        holder.setItemClickListener {
            val prevSelectedItemPosition = selectedItemPosition
            selectedItemPosition = position
            notifyItemChanged(position)
            if (prevSelectedItemPosition != null) {
                notifyItemChanged(prevSelectedItemPosition)
            }
        }
        if (position == selectedItemPosition) {
            holder.showGalleryItemActions()
        } else {
            holder.hideGalleryItemActions()
        }
    }

    fun set(itemsToSet: List<GalleryItem>) {
        items.clear()
        items.addAll(itemsToSet)
        notifyDataSetChanged()
    }

    fun append(itemsToAppend: List<GalleryItem>) {
        items.addAll(itemsToAppend)
        notifyItemRangeChanged(items.size - itemsToAppend.size, itemsToAppend.size)
    }

    interface GalleryItemActions {
        fun show(path: String)
        fun share(path: String)
        fun delete(path: String)
    }
}
