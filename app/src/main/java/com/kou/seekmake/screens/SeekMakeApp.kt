package com.kou.seekmake.screens

import android.app.Application
import com.kou.seekmake.common.firebase.FirebaseAuthManager
import com.kou.seekmake.data.firebase.*
import com.kou.seekmake.data.retrofit.SeekMakeRepository
import com.kou.seekmake.screens.notifications.NotificationsCreator
import com.kou.seekmake.screens.search.SearchPostsCreator

class SeekMakeApp : Application() {
    val usersRepo by lazy { FirebaseUsersRepository() }
    val feedPostsRepo by lazy { FirebaseFeedPostsRepository() }
    val notificationsRepo by lazy { FirebaseNotificationsRepository() }
    val authManager by lazy { FirebaseAuthManager() }
    val searchRepo by lazy { FirebaseSearchRepository() }
    val seekMakeRepo by lazy { SeekMakeRepository() }
    val storyRepo by lazy { FirebaseStoryRepository() }

    override fun onCreate() {
        super.onCreate()
        NotificationsCreator(notificationsRepo, usersRepo, feedPostsRepo)
        SearchPostsCreator(searchRepo)
    }
}