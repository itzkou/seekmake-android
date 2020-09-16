package com.kou.seekmake.screens.profile_other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseViewModel

class OtherViewModel(private val usersRepo: UsersRepository,
                     onFailureListener: OnFailureListener) :
        BaseViewModel(onFailureListener) {
    var otherUser: LiveData<User> = MutableLiveData()
    lateinit var images: LiveData<List<String>>
    fun init(uid: String) {
        if (!this::images.isInitialized) {
            images = usersRepo.getImages(uid)
        }
    }

    fun getOther(uid: String): LiveData<User> {
        otherUser = usersRepo.getUser(uid)
        return otherUser

    }
}