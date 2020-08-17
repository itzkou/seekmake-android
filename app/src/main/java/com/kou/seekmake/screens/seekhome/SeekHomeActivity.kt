package com.kou.seekmake.screens.seekhome

import android.content.Intent
import android.os.Bundle
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.common.setupAuthGuard
import com.kou.seekmake.screens.common.setupBottomNavigation
import com.kou.seekmake.screens.order.OrderActivity
import com.kou.seekmake.screens.register.SeekRegisterViewModel
import kotlinx.android.synthetic.main.activity_seek_home.*

class SeekHomeActivity : BaseActivity() {
    private lateinit var mViewModel: SeekRegisterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seek_home)

        setupAuthGuard { uid ->
            setupBottomNavigation(uid, 0)
            btn_upload.setOnClickListener {
                startActivity(Intent(this, OrderActivity::class.java))
            }


        }
    }
}