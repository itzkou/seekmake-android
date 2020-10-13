package com.kou.seekmake.screens.register

import android.app.Application
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.R
import com.kou.seekmake.common.SingleLiveEvent
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseViewModel
import com.kou.seekmake.screens.common.CommonViewModel

class RegisterViewModel(private val commonViewModel: CommonViewModel,
                        private val app: Application,
                        onFailureListener: OnFailureListener,
                        private val usersRepo: UsersRepository) : BaseViewModel(onFailureListener) {
    private var email: String? = null
    private var fullName: String? = null
    private var password: String? = null

    private val _goToSeekRegister = SingleLiveEvent<Unit>()
    val goToSeekRegister = _goToSeekRegister

    fun onEmailEntered(email: String, fullName: String, password: String) {
        if (email.isNotEmpty()) {
            this.email = email
            this.fullName = fullName
            this.password = password
            usersRepo.isUserExistsForEmail(email).addOnSuccessListener { exists ->
                if (!exists) {
                    onRegister(fullName, password)
                } else {
                    commonViewModel.setErrorMessage(app.getString(R.string.this_email_already_exists))
                }
            }.addOnFailureListener(onFailureListener)
        } else {
            commonViewModel.setErrorMessage(app.getString(R.string.please_enter_email))
        }

    }

    private fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            val localEmail = email
            if (localEmail != null) {
                usersRepo.createUser(mkUser(fullName, localEmail), password).addOnSuccessListener {
                    _goToSeekRegister.call()
                }.addOnFailureListener(onFailureListener)
            } else {
                Log.e(RegisterActivity.TAG, "onRegister: email is null")
                commonViewModel.setErrorMessage(app.getString(R.string.please_enter_email))
            }
        } else {
            commonViewModel.setErrorMessage(app.getString(R.string.please_enter_fullname_and_password))
        }
    }

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun mkUsername(fullName: String) =
            fullName.toLowerCase().replace(" ", ".")
}