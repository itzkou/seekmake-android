package com.kou.seekmake.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.data.firebase.SearchRepository
import com.kou.seekmake.models.Firebase.SearchPost
import com.kou.seekmake.screens.common.BaseViewModel

class SearchViewModel(searchRepo: SearchRepository,
                      onFailureListener: OnFailureListener) : BaseViewModel(onFailureListener) {
    private val searchText = MutableLiveData<String>()

    val posts: LiveData<List<SearchPost>> = Transformations.switchMap(searchText) { text ->
        searchRepo.searchPosts(text)
    }

    fun setSearchText(text: String) {
        searchText.value = text
    }
}