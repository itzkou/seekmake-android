package com.kou.seekmake.screens.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.data.retrofit.SeekMakeApi
import com.kou.seekmake.data.retrofit.SeekMakeRepository
import com.kou.seekmake.models.SeekMake.SignUpResponse
import com.kou.seekmake.models.SeekMake.UserSeek
import com.kou.seekmake.screens.common.BaseViewModel

class SeekRegisterViewModel(private val repository: SeekMakeRepository, onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {

    private var signUpResponse: LiveData<SignUpResponse> = MutableLiveData<SignUpResponse>()

    private val api = SeekMakeApi.create()


    fun signUp(user: UserSeek): LiveData<SignUpResponse> {
        signUpResponse = repository.signUp(api, user)
        return signUpResponse
    }


}