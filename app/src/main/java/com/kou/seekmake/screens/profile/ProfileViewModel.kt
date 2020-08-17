package com.kou.seekmake.screens.profile

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.Story
import com.kou.seekmake.screens.common.BaseViewModel

class ProfileViewModel(private val usersRepo: UsersRepository, onFailureListener: OnFailureListener)
    : BaseViewModel(onFailureListener) {
    val user = usersRepo.getUser()
    lateinit var images: LiveData<List<String>>
    lateinit var story: LiveData<Story?>


    fun init(uid: String) {
        if (!this::images.isInitialized) {
            images = usersRepo.getImages(uid)
        }
    }

    fun checkStories(uid: String): LiveData<Story?> {
        story = usersRepo.checkStories(uid)
        return story
    }
}