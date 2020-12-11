package com.kou.seekmake.screens.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.ImagePreviewer
import com.kou.seekmake.screens.common.SimpleCallback
import com.kou.seekmake.screens.common.fitCenter
import kotlinx.android.synthetic.main.search_item.view.*

class SearchAdapter(private val listener: Listener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    interface Listener {
        fun managePost(imageURL: String)
    }

    class ViewHolder(val im: ConstraintLayout) : RecyclerView.ViewHolder(im)

    private var images = listOf<String>()

    fun updateImages(newImages: List<String>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(images, newImages) { it })
        this.images = newImages
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val image = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_item, parent, false) as ConstraintLayout
        return ViewHolder(image)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.im.search_image.fitCenter(images[position])
        holder.im.setOnLongClickListener { v ->
            ImagePreviewer().show(v!!.context, holder.im.search_image)
            false
        }
        holder.im.setOnClickListener {
            listener.managePost(images[position])
        }
    }

    override fun getItemCount(): Int = images.size

}