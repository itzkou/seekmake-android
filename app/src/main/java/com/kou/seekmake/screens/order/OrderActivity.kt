package com.kou.seekmake.screens.order

import android.os.Bundle
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.BaseActivity
import kotlinx.android.synthetic.main.activity_order.*

class OrderActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        btn_laser.setOnClickListener {
            FileLodingActivity.start(this, "laser")
        }
        btn_3d.setOnClickListener {
            FileLodingActivity.start(this, "impression")
        }
        btn_fraisage.setOnClickListener {
            FileLodingActivity.start(this, "fraisage")
        }


    }
}