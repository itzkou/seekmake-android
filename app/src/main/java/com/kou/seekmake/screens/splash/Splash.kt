package com.kou.seekmake.screens.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.viewpager.widget.ViewPager
import com.kou.seekmake.R
import com.kou.seekmake.screens.common.BaseActivity
import com.kou.seekmake.screens.home.HomeActivity
import kotlinx.android.synthetic.main.activity_splash.*

class Splash : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sliderAdapter = SplashAdapter(this)
        slider_pager.adapter = sliderAdapter
        slider_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 2)
                    Handler().postDelayed({
                        startHomeActivity()
                    }, 2500)

            }

        })
        Handler().postDelayed({
            slider_pager.currentItem++
            Handler().postDelayed({
                slider_pager.currentItem++
            }, 2500)
        }, 2500)


    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}