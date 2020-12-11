package com.kou.seekmake.screens.profile_other

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.*
import com.kou.seekmake.screens.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_other.*

class OtherProfileActivity : BaseActivity(), ImagesAdapter.Listener {
    private lateinit var other: User
    private lateinit var mUser: User
    private lateinit var otherUid: String
    private var mFollows = mapOf<String, Boolean>()

    private lateinit var mAdapter: ImagesAdapter
    private lateinit var viewModel: OtherViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)
        otherUid = intent.getStringExtra(UID) ?: return finish()
        rvImages.layoutManager = GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter(this)
        rvImages.adapter = mAdapter

        setupAuthGuard {
            viewModel = initViewModel()
            viewModel.init(otherUid)

            viewModel.getOther(otherUid).observe(this, Observer {
                it?.let {
                    other = it
                    profile_image.loadUserPhoto(other.photo)
                    procopy.loadblur(other.photo)
                    username_text.text = other.username
                    followers_count_text.text = it.followers.size.toString()
                    following_count_text.text = it.follows.size.toString()
                }

            })

            viewModel.images.observe(this, Observer {
                it?.let { images ->
                    mAdapter.updateImages(images)
                    posts_count_tx.text = images.size.toString()
                }
            })
            /*** follow system **/
            viewModel.userAndFriends.observe(this, Observer {
                it?.let { (user, _) ->
                    mUser = user
                    mFollows = mUser.follows
                    val follows = mFollows[otherUid] ?: false
                    if (follows) {
                        follow_btn.visibility = View.GONE
                        unfollow_btn.visibility = View.VISIBLE
                    } else {
                        follow_btn.visibility = View.VISIBLE
                        unfollow_btn.visibility = View.GONE
                    }
                }
            })

            follow_btn.setOnClickListener {
                setFollow(otherUid, true) {
                    Toast.makeText(this, "Followed", Toast.LENGTH_SHORT).show()
                }
            }

            unfollow_btn.setOnClickListener {
                setFollow(otherUid, false) {
                    Toast.makeText(this, "Unfollowed", Toast.LENGTH_SHORT).show()

                }
            }


        }
    }

    companion object {
        private const val UID = "UID"

        fun start(context: Context, uid: String) {
            val intent = Intent(context, OtherProfileActivity::class.java)
            val intentpro = Intent(context, ProfileActivity::class.java)
            if (uid != FirebaseAuth.getInstance().currentUser?.uid) {
                intent.putExtra(UID, uid)
                context.startActivity(intent)
            } else
                context.startActivity(intentpro)
        }
    }


    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {
        viewModel.setFollow(mUser.uid, uid, follow)
                .addOnSuccessListener { onSuccess() }
    }

    override fun managePost(imageURL: String) {
        DetailActivity.start(this, imageURL)
    }
}