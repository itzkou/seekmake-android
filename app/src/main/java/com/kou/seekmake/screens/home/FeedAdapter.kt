package com.kou.seekmake.screens.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.FeedPost
import com.kou.seekmake.screens.common.SimpleCallback
import com.kou.seekmake.screens.common.loadImageRounded
import com.kou.seekmake.screens.common.loadUserPhoto
import com.kou.seekmake.screens.common.setCaptionText
import kotlinx.android.synthetic.main.feed_item.view.*


class FeedAdapter(private val listener: Listener)
    : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

    interface Listener {
        fun toggleLike(postId: String)
        fun loadLikes(postId: String, position: Int)
        fun openComments(postId: String, postImage: String, postUid: String)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private var posts = listOf<FeedPost>()
    private var postLikes: Map<Int, FeedPostLikes> = emptyMap()
    private val defaultPostLikes = FeedPostLikes(0, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_item, parent, false)
        return ViewHolder(view)
    }

    fun updatePostLikes(position: Int, likes: FeedPostLikes) {
        postLikes += (position to likes)
        notifyItemChanged(position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = posts[position]
        val likes = postLikes[position] ?: defaultPostLikes
        with(holder.view) {
            user_photo_image.loadUserPhoto(post.avatar)
            username_text.text = post.username
            post_image.loadImageRounded(post.image[0])
            if (likes.likesCount == 0) {
                likes_text.visibility = View.GONE
            } else {
                likes_text.visibility = View.VISIBLE
                val likesCountText = holder.view.context.resources.getQuantityString(
                        R.plurals.likes_count, likes.likesCount, likes.likesCount)
                likes_text.text = likesCountText
            }
            caption_text.setCaptionText(post.username, post.caption)
            like_image.setOnClickListener { listener.toggleLike(post.id) }
            like_image.setImageResource(
                    if (likes.likedByUser) R.drawable.ic_hearte
                    else R.drawable.ic_heartd)
            comment_image.setOnClickListener { listener.openComments(post.id, post.image[0], post.uid) }
            listener.loadLikes(post.id, position)
        }

    }

    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<FeedPost>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(this.posts, newPosts) { it.id })
        this.posts = newPosts
        diffResult.dispatchUpdatesTo(this)
    }

}