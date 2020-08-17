package com.kou.seekmake.data.firebase

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.kou.seekmake.models.Firebase.SearchPost

interface SearchRepository {
    fun searchPosts(text: String): LiveData<List<SearchPost>>
    fun createPost(post: SearchPost): Task<Unit>
}