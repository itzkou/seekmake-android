package com.kou.seekmake.screens.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.R
import com.kou.seekmake.common.AuthManager
import com.kou.seekmake.common.SingleLiveEvent
import com.kou.seekmake.data.retrofit.SeekMakeApi
import com.kou.seekmake.data.retrofit.SeekMakeRepository
import com.kou.seekmake.models.SeekMake.LoginResponse
import com.kou.seekmake.models.SeekMake.UserSeek
import com.kou.seekmake.screens.common.BaseViewModel
import com.kou.seekmake.screens.common.CommonViewModel

class LoginViewModel(private val repository: SeekMakeRepository, private val authManager: AuthManager,
                     private val app: Application,
                     private val commonViewModel: CommonViewModel,
                     onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {
    private var signInResponse: LiveData<LoginResponse> = MutableLiveData<LoginResponse>()
    private val api = SeekMakeApi.create()
    private val _goToSeekLogin = SingleLiveEvent<Unit>()
    val goToSeekLogin: LiveData<Unit> = _goToSeekLogin
    private val _goToRegisterScreen = SingleLiveEvent<Unit>()
    val goToRegisterScreen: LiveData<Unit> = _goToRegisterScreen

    fun onLoginClick(email: String, password: String) {
        if (validate(email, password)) {
            authManager.signIn(email, password).addOnSuccessListener {
                _goToSeekLogin.value = Unit
            }.addOnFailureListener(onFailureListener)
        } else {
            commonViewModel.setErrorMessage(app.getString(R.string.please_enter_email_and_password))
        }
    }

    private fun validate(email: String, password: String) =
            email.isNotEmpty() && password.isNotEmpty()

    fun onRegisterClick() {
        _goToRegisterScreen.call()
    }

    fun signIn(user: UserSeek): LiveData<LoginResponse> {
        signInResponse = repository.signIn(api, user)

        return signInResponse
    }
}