package com.kou.seekmake.screens.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AbsListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.comments.CommentsActivity
import com.kou.seekmake.screens.common.*
import com.kou.seekmake.screens.profile_other.OtherProfileActivity
import com.kou.seekmake.screens.search.SearchActivity
import com.kou.seekmake.screens.stories.OpenStoriesActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.top_bar.*


class HomeActivity : BaseActivity(), FeedAdapter.Listener, FollowerAdapter.Listener {
    private lateinit var mAdapter: FeedAdapter
    private lateinit var mFriendsAdapter: FollowerAdapter
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mUser: User
    lateinit var postDialog: AlertDialog
    private var QUERY_PAGE_SIZE: Int = 4

    var isLastPage = false
    var isScrolling = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupBlur(topBlur, this, window)

        /**** feed ***/
        mAdapter = FeedAdapter(this)
        feed_recycler.adapter = mAdapter
        feed_recycler.layoutManager = LinearLayoutManager(this)

        /*** Followers ***/
        mFriendsAdapter = FollowerAdapter(this)
        rvfriends.adapter = mFriendsAdapter
        rvfriends.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        setupAuthGuard { uid ->
            setupBottomNavigation(uid, 1)
            mViewModel = initViewModel()
            //todo pagination
            mViewModel.init(uid, 100)
            mViewModel.feedPosts.observe(this, Observer { posts ->

                posts?.let {

                    mAdapter.updatePosts(posts)
                }
            })

            val scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount

                    val isNotLastPage = !isLastPage
                    val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                    val isNotAtBeginning = firstVisibleItemPosition >= 0
                    val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

                    val shouldPaginate = isAtLastItem && isNotAtBeginning
                    if (shouldPaginate) {
                        Toast.makeText(this@HomeActivity, "Should Paginate", Toast.LENGTH_SHORT).show()
                        isScrolling = false
                    } else {
                        Toast.makeText(this@HomeActivity, "Should not Paginate", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        isScrolling = true
                    }
                }
            }
            feed_recycler.addOnScrollListener(scrollListener)


            mViewModel.goToCommentsScreen.observe(this, Observer {
                it?.let { postId ->
                    CommentsActivity.start(this, postId)
                }
            })



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

    override fun goUser(uid: String) {
        OtherProfileActivity.start(this, uid)
    }

    override fun openDialog(uid: String, postId: String, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.builder_post, null)
        val txDelete = dialogView.findViewById<TextView>(R.id.txDelete)
        val txReport = dialogView.findViewById<TextView>(R.id.txReport)

        val builder = AlertDialog.Builder(this)

        builder.setView(dialogView)
        postDialog = builder.create()
        postDialog.show()
        txDelete.setOnClickListener {
            mViewModel.deleteFeedPost(uid, postId).addOnSuccessListener {
                postDialog.dismiss()
            }
        }

        txReport.setOnClickListener {
            postDialog.dismiss()
            val snacko = Snackbar.make(baro, "We will come back to you soon !", Snackbar.LENGTH_SHORT)
            snacko.config(baro.context)
            snacko.show()
        }


    }

    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {
        mViewModel.setFollow(mUser.uid, uid, follow)
                .addOnSuccessListener { onSuccess() }
    }


}