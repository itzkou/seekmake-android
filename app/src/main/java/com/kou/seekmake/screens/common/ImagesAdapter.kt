package com.kou.seekmake.screens.common

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kou.seekmake.R

class ImagesAdapter(private val listener: ImagesAdapter.Listener) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {
    interface Listener {
        fun managePost()
    }

    class ViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image)

    private var images = listOf<String>()

    fun updateImages(newImages: List<String>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(images, newImages) { it })
        this.images = newImages
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false) as ImageView
        return ViewHolder(image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.loadImage(images[position])
        holder.image.setOnLongClickListener { v ->
            ImagePreviewer().show(v!!.context, holder.image)
            false
        }
        holder.image.setOnClickListener {
            listener.managePost()
        }
    }

    override fun getItemCount(): Int = images.size
}