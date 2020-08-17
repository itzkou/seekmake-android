package com.kou.seekmake.screens.stories

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks
import com.kou.seekmake.common.SingleLiveEvent
import com.kou.seekmake.data.firebase.StoryRepository
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.Story
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseViewModel

class StoryViewModel(private val storyRepo: StoryRepository, private val usersRepo: UsersRepository, onFailureListener: OnFailureListener)
    : BaseViewModel(onFailureListener) {
    private val _shareCompletedEvent = SingleLiveEvent<Unit>()
    val shareCompletedEvent = _shareCompletedEvent
    val user = usersRepo.getUser()
    lateinit var stories: LiveData<List<Story>>

    fun share(user: User, imageUri: Uri?) {
        if (imageUri != null) {

            usersRepo.uploadUserStory(user.uid, imageUri).onSuccessTask { downloadUrl ->
                Tasks.whenAll(
                        usersRepo.setUserStory(user.uid, downloadUrl!!),
                        storyRepo.createStory(user.uid, mkStory(user,
                                downloadUrl.toString()))
                )


            }.addOnCompleteListener {
                _shareCompletedEvent.call()
            }.addOnFailureListener(onFailureListener)
        }
    }

    fun getStories(id: String): LiveData<List<Story>> {
        if (!this::stories.isInitialized)
            stories = storyRepo.getStories(id)
        return stories
    }


    private fun mkStory(user: User, imageDownloadUrl: String): Story {
        return Story(
                uid = user.uid,
                username = user.username,
                image = imageDownloadUrl,
                avatar = user.photo
        )
    }
}