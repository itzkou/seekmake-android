package com.kou.seekmake.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kou.seekmake.models.Firebase.Comment
import com.kou.seekmake.models.Firebase.FeedPost
import com.kou.seekmake.models.Firebase.Story

object EventBus {
    private val liveDataBus = MutableLiveData<Event>()

    val events: LiveData<Event> = liveDataBus

    fun publish(event: Event) {
        liveDataBus.value = event
    }
}

sealed class Event {
    data class CreateComment(val postId: String, val comment: Comment) : Event()
    data class CreateLike(val postId: String, val uid: String) : Event()
    data class CreateFollow(val fromUid: String, val toUid: String) : Event()
    data class CreateFeedPost(val post: FeedPost) : Event()
    data class CreateStory(val story: Story) : Event()
}