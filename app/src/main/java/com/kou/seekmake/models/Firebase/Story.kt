package com.kou.seekmake.models.Firebase

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class Story(val uid: String = "", val username: String = "",
                 val image: String = "",
                 val timestamp: Any = ServerValue.TIMESTAMP, val avatar: String? = null, val story: String? = null,
                 @get:Exclude val id: String = "") {
    fun timestampDate(): Date = Date(timestamp as Long)
}