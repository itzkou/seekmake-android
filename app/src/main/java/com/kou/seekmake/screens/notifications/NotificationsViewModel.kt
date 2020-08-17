package com.kou.seekmake.screens.notifications

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.kou.seekmake.data.firebase.NotificationsRepository
import com.kou.seekmake.data.firebase.StoryRepository
import com.kou.seekmake.models.Firebase.Notification
import com.kou.seekmake.screens.common.BaseViewModel

class NotificationsViewModel(private val notificationsRepo: NotificationsRepository, private val storyRepo: StoryRepository,
                             onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {
    lateinit var notifications: LiveData<List<Notification>>
    private lateinit var uid: String

    fun init(uid: String) {
        if (!this::uid.isInitialized) {
            this.uid = uid
            notifications = notificationsRepo.getNotifications(uid)
            //deleteStories(uid)
        }
    }

    fun setNotificationsRead(notifications: List<Notification>) {
        val ids = notifications.filter { !it.read }.map { it.id }
        if (ids.isNotEmpty()) {
            notificationsRepo.setNotificationsRead(uid, ids, true)
                    .addOnFailureListener(onFailureListener)
        }
    }

    fun deleteStories(id: String): Task<Unit> {
        return storyRepo.deleteStory(id)

    }

}