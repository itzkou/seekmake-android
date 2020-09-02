package com.kou.seekmake.screens.share

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
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


    fun share(user: User, imageUris: List<Uri>, caption: String) {
        val collectedUrls = arrayListOf<String>()
        var counter = 0
        for (imageUri in imageUris) {

            usersRepo.uploadUserImage(user.uid, imageUri).onSuccessTask { downloadUrl ->
                collectedUrls.add(downloadUrl.toString())
                usersRepo.setUserImage(user.uid, downloadUrl!!)

            }.addOnCompleteListener {

                if (counter == imageUris.size - 1)
                    feedPostsRepo.createFeedPost(user.uid, mkFeedPost(user, caption, collectedUrls)).addOnSuccessListener {
                        _shareCompletedEvent.call()

                    }
                counter++
            }.addOnFailureListener(onFailureListener)

        }


    }

    private fun mkFeedPost(user: User, caption: String, imageDownloadUrl: List<String>): FeedPost {
        return FeedPost(
                uid = user.uid,
                username = user.username,
                image = imageDownloadUrl,
                caption = caption,
                avatar = user.photo
        )
    }
}