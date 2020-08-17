package com.kou.seekmake.screens.stories

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import com.kou.seekmake.R
import com.kou.seekmake.data.firebase.common.FirebaseHelper
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.CameraHelper
import com.kou.seekmake.screens.common.loadImage
import com.kou.seekmake.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_share.*

class StoryActivity : BaseActivity() {
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User
    private lateinit var mViewModel: StoryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)


        setupAuthGuard {
            mViewModel = initViewModel()
            mFirebase = FirebaseHelper(this)

            mCamera = CameraHelper(this)
            mCamera.takeCameraPicture()

            back_image.setOnClickListener { finish() }
            share_text.setOnClickListener { share() }

            mViewModel.user.observe(this, Observer {
                it?.let {
                    mUser = it
                }
            })
            mViewModel.shareCompletedEvent.observe(this, Observer {
                finish()
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                post_image.loadImage(mCamera.imageUri?.toString())
            } else {
                finish()
            }
        }
    }


    private fun share() {
        mViewModel.share(mUser, mCamera.imageUri)
    }

}