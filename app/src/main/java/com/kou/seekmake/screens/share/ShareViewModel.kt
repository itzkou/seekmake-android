package com.kou.seekmake.screens.share

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Tasks
import com.kou.seekmake.common.SingleLiveEvent
import com.kou.seekmake.data.firebase.FeedPostsRepository
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.FeedPost
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseViewModel

class ShareViewModel(private val feedPostsRepo: FeedPostsRepository,
                     private val usersRepo: UsersRepository,
                     onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {
    private val _shareCompletedEvent = SingleLiveEvent<Unit>()
    val shareCompletedEvent = _shareCompletedEvent
    val user = usersRepo.getUser()


    fun share(user: User, imageUri: Uri?, caption: String) {
        if (imageUri != null) {

            usersRepo.uploadUserImage(user.uid, imageUri).onSuccessTask { downloadUrl ->
                Tasks.whenAll(
                        usersRepo.setUserImage(user.uid, downloadUrl!!),
                        feedPostsRepo.createFeedPost(user.uid, mkFeedPost(user, caption,
                                downloadUrl.toString()))
                )


            }.addOnCompleteListener {
                _shareCompletedEvent.call()
            }.addOnFailureListener(onFailureListener)
        }
    }

    private fun mkFeedPost(user: User, caption: String, imageDownloadUrl: String): FeedPost {
        return FeedPost(
                uid = user.uid,
                username = user.username,
                image = imageDownloadUrl,
                caption = caption,
                avatar = user.photo
        )
    }
}