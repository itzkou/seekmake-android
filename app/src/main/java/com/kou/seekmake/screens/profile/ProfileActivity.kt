package com.kou.seekmake.screens.profile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.Observer
import com.kou.seekmake.R
import com.kou.seekmake.screens.addfriends.AddFriendsActivity
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.loadUserPhoto
import com.kou.seekmake.screens.common.setupAuthGuard
import com.kou.seekmake.screens.common.setupBottomNavigation
import com.kou.seekmake.screens.editprofile.EditProfileActivity
import com.kou.seekmake.screens.profile.Fragments.Images
import com.kou.seekmake.screens.profile.Fragments.Quotes
import com.kou.seekmake.screens.stories.OpenStoriesActivity
import com.kou.seekmake.screens.stories.StoryActivity
import com.mxn.soul.flowingdrawer_core.ElasticDrawer
import com.mxn.soul.flowingdrawer_core.FlowingDrawer
import kotlinx.android.synthetic.main.activity_profile.*


class ProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val mDrawer = findViewById<FlowingDrawer>(R.id.drawerlayout)
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL)


        add_story.setOnClickListener {
            startActivity(Intent(this, StoryActivity::class.java))
        }
        edit_profile_btn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
        settings_image.setOnClickListener {
            mDrawer.openMenu()
        }
        add_friends_image.setOnClickListener {
            val intent = Intent(this, AddFriendsActivity::class.java)
            startActivity(intent)
        }

        /*** setup viewpager ***/
        val adapter = VpProfileAdapter(supportFragmentManager)
        val images = Images.newInstance()
        val quotes = Quotes.newInstance()
        val loading = Images.newInstance()


        adapter.addFragment(images)
        adapter.addFragment(quotes)
        adapter.addFragment(loading)

        vp_profile.adapter = adapter

        tabs.setupWithViewPager(vp_profile)





        setupAuthGuard { uid ->

            setupBottomNavigation(uid, 4)
            val viewModel = initViewModel<ProfileViewModel>()
            viewModel.init(uid)
            viewModel.checkStories(uid).observe(this, Observer {
                if (it != null) {
                    profile_image.isEnabled = true
                    profile_image.isHighlighted = true
                    profile_image.setOnClickListener {
                        profile_image.numberOfArches = 10
                        profile_image.isAnimating = true
                        Handler().postDelayed({
                            profile_image.isAnimating = false
                            openStories(uid)
                        }, 1800)

                    }

                } else {
                    profile_image.isHighlighted = false
                    profile_image.isEnabled = false
                }
            })
            viewModel.user.observe(this, Observer {
                it?.let {
                    profile_image.loadUserPhoto(it.photo)
                    username_text.text = it.username
                    bio_label.text = it.bio
                    followers_count_text.text = it.followers.size.toString()
                    following_count_text.text = it.follows.size.toString()
                }
            })
            viewModel.images.observe(this, Observer {
                it?.let { images ->
                    posts_count_text.text = images.size.toString()
                }
            })


        }
    }

    companion object {
        const val TAG = "ProfileActivity"
    }

    fun openStories(uid: String) {
        val intent = Intent(this, OpenStoriesActivity::class.java)
        intent.putExtra("user_uid", uid)
        startActivity(intent)
    }


}