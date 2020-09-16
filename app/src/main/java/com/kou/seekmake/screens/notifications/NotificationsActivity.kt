package com.kou.seekmake.screens.notifications

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.setupAuthGuard
import com.kou.seekmake.screens.common.setupBlur
import com.kou.seekmake.screens.common.setupBottomNavigation
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.top_bar.*

class NotificationsActivity : BaseActivity() {
    private lateinit var mAdapter: NotificationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        setupBlur(topBlur, this, window)
        setupAuthGuard { uid ->
            setupBottomNavigation(uid, 3)
            mAdapter = NotificationsAdapter()
            notifications_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            notifications_recycler.adapter = mAdapter

            val viewModel = initViewModel<NotificationsViewModel>()
            viewModel.init(uid)
            viewModel.notifications.observe(this, Observer {
                it?.let {
                    mAdapter.updateNotifications(it)
                    viewModel.setNotificationsRead(it)
                }
            })
        }
    }

    companion object {
        const val TAG = "LikesActivity"
    }
}
