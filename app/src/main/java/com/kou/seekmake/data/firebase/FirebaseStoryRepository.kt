package com.kou.seekmake.data.firebase

import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kou.seekmake.common.task
import com.kou.seekmake.common.toUnit
import com.kou.seekmake.data.common.map
import com.kou.seekmake.data.firebase.common.FirebaseLiveData
import com.kou.seekmake.data.firebase.common.database
import com.kou.seekmake.models.Firebase.Story
import java.util.*
import java.util.concurrent.TimeUnit


class FirebaseStoryRepository : StoryRepository {
    override fun createStory(uid: String, story: Story): Task<Unit> =
            database.child("story-user").child(uid).push().setValue(story).toUnit()

    override fun getStories(uid: String): LiveData<List<Story>> =
            FirebaseLiveData(database.child("story-user").child(uid)).map {
                it.children.map { it.asStory()!! }
            }

    override fun getStoriesLink(uid: String): LiveData<List<String>> =
            FirebaseLiveData(database.child("stories").child(uid)).map {
                it.children.map { it.getValue(String::class.java)!! }
            }

    override fun deleteStory(uid: String): Task<Unit> =
            task {

                val refStoryUser = database.child("story-user").child(uid)

                val cutoff: Long = Date().time - TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
                refStoryUser.orderByChild("timestamp").endAt(cutoff.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (item in snapshot.children) {
                            item.ref.removeValue()

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        throw databaseError.toException()
                    }
                })


            }

    private fun DataSnapshot.asStory(): Story? =
            getValue(Story::class.java)?.copy(id = key!!)
}



