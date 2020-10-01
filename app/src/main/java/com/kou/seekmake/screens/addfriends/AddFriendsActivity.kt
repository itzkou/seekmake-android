package com.kou.seekmake.screens.addfriends

import android.os.Bundle
import androidx.lifecycle.Observer
import com.azoft.carousellayoutmanager.CarouselLayoutManager
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.azoft.carousellayoutmanager.CenterScrollListener
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.setupAuthGuard
import kotlinx.android.synthetic.main.activity_add_friends.*

class AddFriendsActivity : BaseActivity(), FriendsAdapter.Listener {
    private lateinit var mUser: User
    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mViewModel: AddFriendsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friends)
        mAdapter = FriendsAdapter(this)
        add_friends_recycler.apply {
            val newlayoutmger = CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false)
            layoutManager = newlayoutmger
            newlayoutmger.setPostLayoutListener(CarouselZoomPostLayoutListener())
            addOnScrollListener(CenterScrollListener())
            adapter = mAdapter
        }
        setupAuthGuard {
            mViewModel = initViewModel()

            back_image.setOnClickListener { finish() }


            mViewModel.userAndFriends.observe(this, Observer {
                it?.let { (user, otherUsers) ->
                    mUser = user
                    mAdapter.update(otherUsers, mUser.follows)
                }
            })
        }
    }

    override fun follow(uid: String) {
        setFollow(uid, true) {
            mAdapter.followed(uid)
        }
    }

    override fun unfollow(uid: String) {
        setFollow(uid, false) {
            mAdapter.unfollowed(uid)
        }
    }

    private fun setFollow(uid: String, follow: Boolean, onSuccess: () -> Unit) {
        mViewModel.setFollow(mUser.uid, uid, follow)
                .addOnSuccessListener { onSuccess() }
    }
}