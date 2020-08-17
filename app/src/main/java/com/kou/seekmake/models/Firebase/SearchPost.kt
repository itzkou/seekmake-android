package com.kou.seekmake.models.Firebase

import com.google.firebase.database.Exclude

data class SearchPost(val image: String = "", val caption: String = "", val postId: String = "",
                      val avatar: String? = null, val username: String = "", @get:Exclude val id: String = "")