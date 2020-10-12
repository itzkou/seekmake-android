package com.kou.seekmake.screens.common

import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.Notification
import com.kou.seekmake.models.Firebase.NotificationType
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager
import com.kou.seekmake.screens.home.HomeActivity
import com.kou.seekmake.screens.notifications.NotificationsActivity
import com.kou.seekmake.screens.notifications.NotificationsViewModel
import com.kou.seekmake.screens.profile.ProfileActivity
import com.kou.seekmake.screens.seekhome.SeekHomeActivity
import com.kou.seekmake.screens.share.ShareActivity
import com.nhaarman.supertooltips.ToolTip
import com.nhaarman.supertooltips.ToolTipRelativeLayout
import com.nhaarman.supertooltips.ToolTipView
import kotlinx.android.synthetic.main.bottom_navigation_view.*
import kotlinx.android.synthetic.main.notifications_tooltip_content.view.*

class InstagramBottomNavigation(private val uid: String,
                                private val bnv: BottomNavigationViewEx,
                                private val tooltipLayout: ToolTipRelativeLayout,
                                private val navNumber: Int,
                                private val activity: BaseActivity) : LifecycleObserver {

    private lateinit var mViewModel: NotificationsViewModel
    private lateinit var mNotificationsContentView: View
    private var lastTooltipView: ToolTipView? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        mViewModel = activity.initViewModel()
        mViewModel.init(uid)
        mViewModel.notifications.observe(activity, Observer {
            it?.let {
                showNotifications(it)
            }
        })
        mViewModel.deleteStories(uid).addOnSuccessListener {
            Log.d("storiz", "deleted")
        }
        mNotificationsContentView = activity.layoutInflater.inflate(
                R.layout.notifications_tooltip_content, null, false)
    }

    private fun showNotifications(notifications: List<Notification>) {
        if (lastTooltipView != null) {
            val parent = mNotificationsContentView.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(mNotificationsContentView)
                lastTooltipView?.remove()
            }
            lastTooltipView = null
        }

        val newNotifications = notifications.filter { !it.read }
        val newNotificationsMap = newNotifications
                .groupBy { it.type }
                .mapValues { (_, values) -> values.size }

        fun setCount(image: ImageView, textView: TextView, type: NotificationType) {
            val count = newNotificationsMap[type] ?: 0
            if (count == 0) {
                image.visibility = View.GONE
                textView.visibility = View.GONE
            } else {
                image.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                textView.text = count.toString()
            }
        }

        with(mNotificationsContentView) {
            setCount(likes_image, likes_count_text, NotificationType.Like)
            setCount(follows_image, follows_count_text, NotificationType.Follow)
            setCount(comments_image, comments_count_text, NotificationType.Comment)
        }

        if (newNotifications.isNotEmpty()) {
            val tooltip = ToolTip()
                    .withColor(ContextCompat.getColor(activity, R.color.red))
                    .withContentView(mNotificationsContentView)
                    .withAnimationType(ToolTip.AnimationType.FROM_TOP)
                    .withShadow()
            lastTooltipView = tooltipLayout.showToolTipForView(tooltip, bnv.getIconAt(NOTIFICATIONS_ICON_POS))
            lastTooltipView?.setOnToolTipViewClickedListener {
                mViewModel.setNotificationsRead(newNotifications)
                bnv.getBottomNavigationItemView(NOTIFICATIONS_ICON_POS).callOnClick()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        bnv.menu.getItem(navNumber).isChecked = true
        mViewModel.deleteStories(uid)
    }

    init {
        bnv.setIconSize(22f)
        bnv.setTextVisibility(false)
        bnv.enableItemShiftingMode(false)
        bnv.enableShiftingMode(false)
        bnv.enableAnimation(false)
        for (i in 0 until bnv.menu.size()) {
            bnv.setIconTintList(i, null)
        }
        bnv.setOnNavigationItemSelectedListener {
            val nextActivity =
                    when (it.itemId) {
                        R.id.nav_item_home -> SeekHomeActivity::class.java
                        R.id.nav_item_search -> HomeActivity::class.java
                        R.id.nav_item_share -> ShareActivity::class.java
                        R.id.nav_item_likes -> NotificationsActivity::class.java
                        R.id.nav_item_profile -> ProfileActivity::class.java
                        else -> {
                            Log.e(BaseActivity.TAG, "unknown nav item clicked $it")
                            null
                        }
                    }
            if (nextActivity != null) {
                val intent = Intent(activity, nextActivity)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                activity.startActivity(intent)
                activity.overridePendingTransition(0, 0)
                true
            } else {
                false
            }
        }
    }

    companion object {
        const val NOTIFICATIONS_ICON_POS = 3
    }
}

fun BaseActivity.setupBottomNavigation(uid: String, navNumber: Int) {
    val bnv = InstagramBottomNavigation(uid, bottom_navigation_view, tooltip_layout, navNumber, this)
    //bottom_navigation_view.setIconSize(22f)
    if (PrefsManager.geAvatar(this)!!.isNotEmpty())
        bottom_navigation_view.getIconAt(4).loadImgRound(PrefsManager.geAvatar(this), 12)
    this.lifecycle.addObserver(bnv)
}