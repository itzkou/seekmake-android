package com.kou.seekmake.screens.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.firebase.auth.FirebaseAuth
import com.kou.seekmake.data.firebase.common.auth
import com.kou.seekmake.screens.common.SharedUtils.PrefsManager

class AuthGuard(private val activity: BaseActivity, f: (String) -> Unit) : LifecycleObserver {

    init {
        val user = auth.currentUser
        if (user == null || PrefsManager.geToken(activity) == null) {
            activity.goToLogin()
        } else {
            f(user.uid)
            activity.lifecycle.addObserver(this)
        }
    }

    private val listener = FirebaseAuth.AuthStateListener {
        if (it.currentUser == null) {
            activity.goToLogin()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        auth.addAuthStateListener(listener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        auth.removeAuthStateListener(listener)
    }
}

fun BaseActivity.setupAuthGuard(f: (String) -> Unit) {
    AuthGuard(this, f)
}