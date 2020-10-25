package com.kou.seekmake.screens.seekhome

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kou.seekmake.R
import com.kou.seekmake.models.Firebase.FeedPost
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.setupAuthGuard
import com.kou.seekmake.screens.common.setupBlur
import com.kou.seekmake.screens.common.setupBottomNavigation
import com.kou.seekmake.screens.home.Adapters.FeedAdapter
import com.kou.seekmake.screens.order.OrderActivity
import kotlinx.android.synthetic.main.activity_seek_home.*
import kotlinx.android.synthetic.main.top_bar.*

class SeekHomeActivity : BaseActivity(), FeedAdapter.Listener {
    private lateinit var mAdapter: FeedAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seek_home)
        setupBlur(topBlur, this, window)
        /**** feed ***/
        mAdapter = FeedAdapter(this)
        rvSeek.adapter = mAdapter
        rvSeek.layoutManager = LinearLayoutManager(this)
        setupAuthGuard { uid ->
            setupBottomNavigation(uid, 0)
            btn_upload.setOnClickListener {
                startActivity(Intent(this, OrderActivity::class.java))
            }
            val p1 = FeedPost("p1", "SeekMake", listOf("https://pbs.twimg.com/media/EjRW0coXcBIWcoe?format=png&name=small"), "Promotion 20 %", emptyList(), 1601418895500, "https://pbs.twimg.com/media/EjRW0dwXkAE7yOm?format=png&name=small", "1", 0)
            val p2 = FeedPost("p2", "SeekMake", listOf("https://pbs.twimg.com/media/EjRXTbuWAAY0S2P?format=png&name=small"), "SeekMake is reshaping Architecture by implementing 3D printed models", emptyList(), 1601418895500, "https://pbs.twimg.com/media/EjRW0dwXkAE7yOm?format=png&name=small", "2", 0)
            val p3 = FeedPost("p3", "SeekMake", listOf("https://pbs.twimg.com/media/EjRW0ddWoAEjCB-?format=png&name=small"), "Dear SeekMake community , brace yourselves for new features", emptyList(), 1601418895500, "https://pbs.twimg.com/media/EjRW0dwXkAE7yOm?format=png&name=small", "3", 0)
            val p4 = FeedPost("p4", "SeekMake", listOf("https://pbs.twimg.com/media/EjRW0dBWkAM4gtz?format=png&name=small"), "At SeekMake we emphasize design , prototyping and creativity. Are you a SeekMaker", emptyList(), 1601418895500, "https://pbs.twimg.com/media/EjRW0dwXkAE7yOm?format=png&name=small", "4", 0)

            mAdapter.updatePosts(listOf(p1, p2, p3, p4))
        }
    }

    override fun toggleLike(postId: String) {

    }

    override fun loadLikes(postId: String, position: Int) {
    }

    override fun openComments(postId: String, postImage: String, postUid: String) {
    }

    override fun openStories(uid: String) {
    }

    override fun goUser(uid: String) {
    }

    override fun openDialog(uid: String, postId: String, position: Int) {
    }
}