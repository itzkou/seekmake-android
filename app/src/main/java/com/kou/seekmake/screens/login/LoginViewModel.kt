package com.kou.seekmake.screens.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.R
import com.kou.seekmake.common.AuthManager
import com.kou.seekmake.common.SingleLiveEvent
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.data.retrofit.SeekMakeApi
import com.kou.seekmake.data.retrofit.SeekMakeRepository
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.models.SeekMake.LoginResponse
import com.kou.seekmake.models.SeekMake.UserSeek
import com.kou.seekmake.screens.common.BaseViewModel
import com.kou.seekmake.screens.common.CommonViewModel

class LoginViewModel(private val seekMakeRepository: SeekMakeRepository, private val usersRepo: UsersRepository, private val authManager: AuthManager,
                     private val app: Application,
                     private val commonViewModel: CommonViewModel,
                     onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {
    private var signInResponse: LiveData<LoginResponse> = MutableLiveData<LoginResponse>()
    private val api = SeekMakeApi.create()
    private val _goToSeekLogin = SingleLiveEvent<Unit>()
    private val _goToRegisterScreen = SingleLiveEvent<Unit>()
    private val _goToHome = SingleLiveEvent<Unit>()
    val goToSeekLogin: LiveData<Unit> = _goToSeekLogin
    val goToRegisterScreen: LiveData<Unit> = _goToRegisterScreen
    val goToHome: LiveData<Unit> = _goToHome

    fun onLoginClick(email: String, password: String) {
        if (validate(email, password)) {
            _goToSeekLogin.call()

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
        signInResponse = seekMakeRepository.signIn(api, user)

        return signInResponse
    }

    /** updates **/

    fun fireAuth(fullName: String, email: String, password: String) {
        usersRepo.isUserExistsForEmail(email).addOnSuccessListener { exists ->
            if (!exists) {
                /** fire sign up **/
                //TODO verify the email already taken onFaillure  using addOnFaillure {..
                usersRepo.createUser(mkUser(fullName, email), password).addOnSuccessListener {
                    authManager.signIn(email, password).addOnSuccessListener {
                        _goToHome.value = Unit
                    }.addOnFailureListener(onFailureListener)

                }.addOnFailureListener(onFailureListener)
            } else {
                /** fire sign in **/
                authManager.signIn(email, password).addOnSuccessListener {
                    _goToHome.value = Unit
                }.addOnFailureListener(onFailureListener)

            }
        }.addOnFailureListener(onFailureListener)
    }

    private fun mkUser(fullName: String, email: String): User {
        val username = mkUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun mkUsername(fullName: String) =
            fullName.toLowerCase().replace(" ", ".")


}