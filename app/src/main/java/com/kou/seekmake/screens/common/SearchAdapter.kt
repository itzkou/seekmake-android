package com.kou.seekmake.screens.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.kou.seekmake.R
import kotlinx.android.synthetic.main.search_item.view.*

class SearchAdapter(private val listener: Listener) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {
    var requestOptions: RequestOptions? = null

    init {
        requestOptions = RequestOptions().fitCenter()

    }

    interface Listener {
        fun managePost()
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
        holder.im.search_image.loadImageRounded(images[position])
        holder.im.setOnLongClickListener { v ->
            ImagePreviewer().show(v!!.context, holder.im.search_image)
            false
        }
        holder.im.setOnClickListener {
            listener.managePost()
        }
    }

    override fun getItemCount(): Int = images.size

}