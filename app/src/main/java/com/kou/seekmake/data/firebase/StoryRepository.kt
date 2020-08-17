package com.kou.seekmake.data.firebase

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.kou.seekmake.models.Firebase.Story

interface StoryRepository {
    fun createStory(uid: String, story: Story): Task<Unit>
    fun getStories(uid: String): LiveData<List<Story>>
    fun getStoriesLink(uid: String): LiveData<List<String>>
    fun deleteStory(uid: String): Task<Unit>

}