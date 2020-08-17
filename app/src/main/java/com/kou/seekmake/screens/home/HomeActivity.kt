package com.kou.seekmake.screens.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.comments.CommentsActivity
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.setupAuthGuard
import com.kou.seekmake.screens.common.setupBottomNavigation
import com.kou.seekmake.screens.search.SearchActivity
import com.kou.seekmake.screens.stories.OpenStoriesActivity
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), FeedAdapter.Listener, FollowerAdapter.Listener {
    private lateinit var mAdapter: FeedAdapter
    private lateinit var mFriendsAdapter: FollowerAdapter
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mUser: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /**** feed ***/
        mAdapter = FeedAdapter(this)
        feed_recycler.adapter = mAdapter
        feed_recycler.layoutManager = LinearLayoutManager(this)
        /*** Followers ***/
        mFriendsAdapter = FollowerAdapter(this)
        setupAuthGuard { uid ->
            setupBottomNavigation(uid, 1)
            mViewModel = initViewModel()
            mViewModel.init(uid)
            mViewModel.feedPosts.observe(this, Observer {

                it?.let {
                    mAdapter.updatePosts(it)
                }
            })
            mViewModel.goToCommentsScreen.observe(this, Observer {
                it?.let { postId ->
                    CommentsActivity.start(this, postId)
                }
            })
            rvfriends.adapter = mFriendsAdapter
            rvfriends.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


            mViewModel.userAndFriends.observe(this, Observer {
                it?.let { (user, otherUsers) ->
                    mUser = user
                    mFriendsAdapter.update(otherUsers, mUser.follows)

                }
            })
            btn_search.setOnClickListener {
                startActivity(Intent(this, SearchActivity::class.java))
            }

        }
    }

    override fun toggleLike(postId: String) {
        Log.d(TAG, "toggleLike: $postId")
        mViewModel.toggleLike(postId)
    }

    override fun loadLikes(postId: String, position: Int) {
        if (mViewModel.getLikes(postId) == null) {
            mViewModel.loadLikes(postId).observe(this, Observer {
                it?.let { postLikes ->
                    mAdapter.updatePostLikes(position, postLikes)
                }
            })
        }
    }

    override fun openComments(postId: String, postImage: String, postUid: String) {
        mViewModel.openComments(postId, postImage, postUid)
    }

    companion object {
        const val TAG = "HomeActivity"
    }

    override fun follow(uid: String) {
        setFollow(uid, true) {
            mFriendsAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false) {
            mFriendsAdapter.unfollowed(uid)
        }
    }

    override fun openStories(uid: String) {
        val intent = Intent(this, OpenStoriesActivity::class.java)
        intent.putExtra("user_uid", uid)
        startActivity(intent)
    }

    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {
        mViewModel.setFollow(mUser.uid, uid, follow)
                .addOnSuccessListener { onSuccess() }
    }
}