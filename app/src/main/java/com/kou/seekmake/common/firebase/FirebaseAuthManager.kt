package com.kou.seekmake.common.firebase

import com.google.android.gms.tasks.Task
import com.kou.seekmake.common.AuthManager
import com.kou.seekmake.common.toUnit
import com.kou.seekmake.data.firebase.common.auth

class FirebaseAuthManager : AuthManager {
    override fun signOut() {
        auth.signOut()
    }

    override fun signIn(email: String, password: String): Task<Unit> =
            auth.signInWithEmailAndPassword(email, password).toUnit()
}