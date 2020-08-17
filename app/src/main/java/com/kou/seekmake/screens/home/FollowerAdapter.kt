package com.kou.seekmake.screens.home

import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kou.seekmake.R
import com.kou.seekmake.data.firebase.common.database
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.SimpleCallback
import com.kou.seekmake.screens.common.loadUserPhoto
import kotlinx.android.synthetic.main.add_friends_item.view.*
import xyz.schwaab.avvylib.AvatarView

class FollowerAdapter(private val listener: Listener)
    : RecyclerView.Adapter<FollowerAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    interface Listener {
        fun follow(uid: String)
        fun unfollow(uid: String)
        fun openStories(uid: String)
    }

    private var mUsers = listOf<User>()
    private var mPositions = mapOf<String, Int>()
    private var mFollows = mapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.add_follower_home, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.view) {
            val user = mUsers[position]
            photo_image.loadUserPhoto(user.photo)
            checkStories(user, photo_image)
            username_text.text = user.username
            username_text.text = user.username
            follow_btn.setOnClickListener { listener.follow(user.uid) }
            unfollow_btn.setOnClickListener { listener.unfollow(user.uid) }

            val follows = mFollows[user.uid] ?: false
            if (follows) {
                follow_btn.visibility = View.GONE
                unfollow_btn.visibility = View.VISIBLE
            } else {
                follow_btn.visibility = View.VISIBLE
                unfollow_btn.visibility = View.GONE
            }
        }
    }

    override fun getItemCount() = mUsers.size

    fun update(users: List<User>, follows: Map<String, Boolean>) {
        val diffResult = DiffUtil.calculateDiff(SimpleCallback(mUsers, users) { it.uid })
        mUsers = users
        mPositions = users.withIndex().map { (idx, user) -> user.uid to idx }.toMap()
        mFollows = follows
        diffResult.dispatchUpdatesTo(this)
    }

    fun followed(uid: String) {
        mFollows = mFollows + (uid to true)
        notifyItemChanged(mPositions[uid]!!)
    }

    fun unfollowed(uid: String) {
        mFollows = mFollows - uid
        notifyItemChanged(mPositions[uid]!!)
    }

    fun checkStories(user: User, photo_image: AvatarView) {
        database.child("story-user").child(user.uid).orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.map {
                    it.value.toString()
                    if (it != null) {
                        photo_image.isHighlighted = true
                        photo_image.setOnClickListener {
                            photo_image.numberOfArches = 10
                            photo_image.isAnimating = true
                            Handler().postDelayed({
                                photo_image.isAnimating = false
                                listener.openStories(user.uid)
                            }, 1800)


                        }

                    } else {
                        photo_image.isHighlighted = false
                    }

                }
            }

        })

    }
}