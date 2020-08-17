package com.kou.seekmake.screens.addfriends

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.kou.seekmake.data.common.map
import com.kou.seekmake.data.firebase.FeedPostsRepository
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseViewModel

class AddFriendsViewModel(onFailureListener: OnFailureListener,
                          private val usersRepo: UsersRepository,
                          private val feedPostsRepo: FeedPostsRepository)
    : BaseViewModel(onFailureListener) {
    val userAndFriends: LiveData<Pair<User, List<User>>> =
            usersRepo.getUsers().map { allUsers ->
                val (userList, otherUsersList) = allUsers.partition {
                    it.uid == usersRepo.currentUid()
                }
                userList.first() to otherUsersList
            }

    fun setFollow(currentUid: String, uid: String, follow: Boolean): Task<Void> {
        return (if (follow) {
            // Returns a Task that completes successfully when all of the specified Tasks complete successfully.
            Tasks.whenAll(
                    usersRepo.addFollow(currentUid, uid),
                    usersRepo.addFollower(currentUid, uid),
                    feedPostsRepo.copyFeedPosts(postsAuthorUid = uid, uid = currentUid))
        } else {
            Tasks.whenAll(
                    usersRepo.deleteFollow(currentUid, uid),
                    usersRepo.deleteFollower(currentUid, uid),
                    feedPostsRepo.deleteFeedPosts(postsAuthorUid = uid, uid = currentUid))
        }).addOnFailureListener(onFailureListener)
    }
}