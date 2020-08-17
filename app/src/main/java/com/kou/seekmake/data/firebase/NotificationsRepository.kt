package com.kou.seekmake.data.firebase

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.kou.seekmake.models.Firebase.Notification

interface NotificationsRepository {
    fun createNotification(uid: String, notification: Notification): Task<Unit>
    fun getNotifications(uid: String): LiveData<List<Notification>>
    fun setNotificationsRead(uid: String, ids: List<String>, read: Boolean): Task<Unit>
}