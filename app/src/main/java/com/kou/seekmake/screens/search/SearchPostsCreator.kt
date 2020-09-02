package com.kou.seekmake.screens.search

import android.util.Log
import androidx.lifecycle.Observer
import com.kou.seekmake.common.BaseEventListener
import com.kou.seekmake.common.Event
import com.kou.seekmake.common.EventBus
import com.kou.seekmake.data.firebase.SearchRepository
import com.kou.seekmake.models.Firebase.SearchPost

class SearchPostsCreator(searchRepo: SearchRepository) : BaseEventListener() {
    init {
        EventBus.events.observe(this, Observer {
            it?.let { event ->
                when (event) {
                    is Event.CreateFeedPost -> {
                        val searchPost = with(event.post) {
                            SearchPost(
                                    image = image[0],
                                    caption = caption,
                                    postId = id,
                                    avatar = avatar,
                                    username = username)
                        }
                        searchRepo.createPost(searchPost).addOnFailureListener {
                            Log.d(TAG, "Failed to create search post for event: $event", it)
                        }
                    }
                    else -> {
                    }
                }
            }
        })
    }

    companion object {
        const val TAG = "SearchPostsCreator"
    }
}