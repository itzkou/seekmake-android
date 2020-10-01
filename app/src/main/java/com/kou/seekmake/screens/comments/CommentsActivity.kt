package com.kou.seekmake.screens.comments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.*
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : BaseActivity() {
    private lateinit var mAdapter: CommentsAdapter
    private lateinit var mUser: User
    private lateinit var other: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val postId = intent.getStringExtra(EXTRA_POST_ID) ?: return finish()
        val postImage = intent.getStringExtra(EXTRA_POST_IM) ?: return finish()
        val postUid = intent.getStringExtra(EXTRA_POST_UID) ?: return finish()




        back_image.setOnClickListener { finish() }

        setupAuthGuard {
            mAdapter = CommentsAdapter()
            comments_recycler.layoutManager = LinearLayoutManager(this)
            comments_recycler.adapter = mAdapter

            val viewModel = initViewModel<CommentsViewModel>()
            viewModel.init(postId)
            viewModel.user.observe(this, Observer {
                it?.let {
                    mUser = it
                    user_photo.loadUserPhoto(mUser.photo)

                }
            })
            viewModel.getOther(postUid).observe(this, Observer {
                it?.let {
                    other = it
                    user_photo_image.loadUserPhoto(other.photo)
                    username_text.text = other.username
                    im_comment.loadImageRounded(postImage)
                    imBlur.loadblur(postImage)

                }
            })
            viewModel.comments.observe(this, Observer {
                it?.let {
                    mAdapter.updateComments(it)
                }
            })
            post_comment_text.setOnClickListener {
                viewModel.createComment(comment_text.text.toString(), mUser)
                comment_text.setText("")
            }
        }
    }

    companion object {
        private const val EXTRA_POST_ID = "POST_ID"
        private const val EXTRA_POST_IM = "POST_IM"
        private const val EXTRA_POST_UID = "POST_US"


        fun start(context: Context, postInfos: List<String?>) {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra(EXTRA_POST_ID, postInfos[0])
            intent.putExtra(EXTRA_POST_IM, postInfos[1])
            intent.putExtra(EXTRA_POST_UID, postInfos[2])

            context.startActivity(intent)
        }
    }
}