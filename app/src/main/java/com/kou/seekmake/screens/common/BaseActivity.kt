package com.kou.seekmake.screens.common

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kou.seekmake.screens.SeekMakeApp
import com.kou.seekmake.screens.login.LoginActivity

abstract class BaseActivity : AppCompatActivity() {
    lateinit var commonViewModel: CommonViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        commonViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)
        commonViewModel.errorMessage.observe(this, Observer {
            it?.let {
                showToast(it)
            }
        })
    }

    inline fun <reified T : BaseViewModel> initViewModel(): T =
            ViewModelProviders.of(this, ViewModelFactory(
                    application as SeekMakeApp,
                    commonViewModel,
                    commonViewModel)).get(T::class.java)

    fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        const val TAG = "BaseActivity"
    }
}