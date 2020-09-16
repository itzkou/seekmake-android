package com.kou.seekmake.screens.profile_other

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.*
import kotlinx.android.synthetic.main.activity_other.*

class OtherProfileActivity : BaseActivity(), ImagesAdapter.Listener {
    private lateinit var other: User
    private lateinit var uid: String
    private lateinit var mAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other)
        uid = intent.getStringExtra(UID) ?: return finish()
        rvImages.layoutManager = GridLayoutManager(this, 3)
        mAdapter = ImagesAdapter(this)
        rvImages.adapter = mAdapter

        setupAuthGuard {
            val viewModel = initViewModel<OtherViewModel>()
            viewModel.init(uid)

            viewModel.getOther(uid).observe(this, Observer {
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

        }
    }

    companion object {
        private const val UID = "UID"

        fun start(context: Context, uid: String) {
            val intent = Intent(context, OtherProfileActivity::class.java)
            intent.putExtra(UID, uid)
            context.startActivity(intent)
        }
    }

    override fun managePost() {
    }
}