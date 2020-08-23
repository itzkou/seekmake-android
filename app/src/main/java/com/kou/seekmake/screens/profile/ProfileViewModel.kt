package com.kou.seekmake.screens.profile

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.data.retrofit.SeekMakeApi
import com.kou.seekmake.data.retrofit.SeekMakeRepository
import com.kou.seekmake.models.Firebase.Story
import com.kou.seekmake.models.SeekMake.DemandsResponse
import com.kou.seekmake.models.SeekMake.UpdateDemandResponse
import com.kou.seekmake.screens.common.BaseViewModel

class ProfileViewModel(private val usersRepo: UsersRepository, private val repository: SeekMakeRepository, onFailureListener: OnFailureListener)
    : BaseViewModel(onFailureListener) {
    val user = usersRepo.getUser()
    lateinit var images: LiveData<List<String>>
    lateinit var story: LiveData<Story?>
    private val api = SeekMakeApi.create()


    fun init(uid: String) {
        if (!this::images.isInitialized) {
            images = usersRepo.getImages(uid)
        }
    }

    fun checkStories(uid: String): LiveData<Story?> {
        story = usersRepo.checkStories(uid)
        return story
    }

    fun getDemands(token: String, id: String): LiveData<DemandsResponse> {
        return repository.getDemands(api, token, id)
    }

    fun updateDemand(id: String): LiveData<UpdateDemandResponse> {
        return repository.updateDemand(api, id)
    }
}