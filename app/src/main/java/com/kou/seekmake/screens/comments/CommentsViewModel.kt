package com.kou.seekmake.screens.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.data.firebase.FeedPostsRepository
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.Comment
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseViewModel

class CommentsViewModel(private val feedPostsRepo: FeedPostsRepository,
                        private val usersRepo: UsersRepository,
                        onFailureListener: OnFailureListener) :
        BaseViewModel(onFailureListener) {
    lateinit var comments: LiveData<List<Comment>>
    private lateinit var postId: String
    val user: LiveData<User> = usersRepo.getUser()
    var otherUser: LiveData<User> = MutableLiveData()


    fun init(postId: String) {
        this.postId = postId
        comments = feedPostsRepo.getComments(postId)
    }

    fun createComment(text: String, user: User) {
        val comment = Comment(
                uid = user.uid,
                username = user.username,
                photo = user.photo,
                text = text)
        feedPostsRepo.createComment(postId, comment).addOnFailureListener(onFailureListener)
    }

    fun getOther(uid: String): LiveData<User> {
        otherUser = usersRepo.getUser(uid)
        return otherUser

    }

}