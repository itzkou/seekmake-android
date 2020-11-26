package com.kou.seekmake.screens.home

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.kou.seekmake.common.SingleLiveEvent
import com.kou.seekmake.data.common.map
import com.kou.seekmake.data.firebase.FeedPostsRepository
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.FeedPost
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseViewModel

class HomeViewModel(onFailureListener: OnFailureListener,
                    private val feedPostsRepo: FeedPostsRepository, private val usersRepo: UsersRepository) : BaseViewModel(onFailureListener) {


    lateinit var uid: String
    lateinit var feedPosts: LiveData<List<FeedPost>>
    private var loadedLikes = mapOf<String, LiveData<FeedPostLikes>>()
    private val _goToCommentsScreen = SingleLiveEvent<List<String?>>()
    val goToCommentsScreen = _goToCommentsScreen
    val userAndFriends: LiveData<Pair<User, List<User>>> =
            usersRepo.getUsers().map { allUsers ->

                val (userList, otherUsersList) = allUsers.partition {
                    it.uid == usersRepo.currentUid()
                }
                userList.first() to otherUsersList
            }


    fun init(uid: String, loadMore: Boolean) {

        var curentPage = 1
        val querySize = 5

        if (loadMore) {

            ++curentPage

            feedPosts = feedPostsRepo.getFeedPosts(uid, querySize, curentPage).map {
                it.sortedByDescending { it.timestampDate() }
            }
            Log.d("lool", feedPosts.value.toString())


        }

        if (!this::uid.isInitialized) {

            this.uid = uid
            feedPosts = feedPostsRepo.getFeedPosts(uid, querySize, curentPage).map {
                it.sortedByDescending { it.timestampDate() }
            }
            Log.d("lool", feedPosts.value.toString())


        }


    }


    fun deleteFeedPost(uid: String, postId: String): Task<Unit> {
        return feedPostsRepo.deleteFeedPost(uid, postId)
    }

    fun toggleLike(postId: String) {
        feedPostsRepo.toggleLike(postId, uid).addOnFailureListener(onFailureListener)
    }

    fun getLikes(postId: String): LiveData<FeedPostLikes>? = loadedLikes[postId]

    fun loadLikes(postId: String): LiveData<FeedPostLikes> {
        val existingLoadedLikes = loadedLikes[postId]
        return if (existingLoadedLikes == null) {
            val liveData = feedPostsRepo.getLikes(postId).map { likes ->
                FeedPostLikes(
                        likesCount = likes.size,
                        likedByUser = likes.find { it.userId == uid } != null)
            }
            loadedLikes = loadedLikes + (postId to liveData)
            liveData
        } else {
            existingLoadedLikes
        }
    }

    fun openComments(postId: String, postImage: String, postUid: String) {
        _goToCommentsScreen.value = arrayListOf(postId, postImage, postUid)
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