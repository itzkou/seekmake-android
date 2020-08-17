package com.kou.seekmake.data.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.kou.seekmake.common.Event
import com.kou.seekmake.common.EventBus
import com.kou.seekmake.common.task
import com.kou.seekmake.common.toUnit
import com.kou.seekmake.data.common.map
import com.kou.seekmake.data.firebase.common.*
import com.kou.seekmake.models.Firebase.Story
import com.kou.seekmake.models.Firebase.User

class FirebaseUsersRepository : UsersRepository {

    override fun uploadUserStory(uid: String, imageUri: Uri): Task<Uri> =
            task { taskSource ->
                storage.child("users").child(uid).child("stories")
                        .child(imageUri.lastPathSegment!!).putFile(imageUri).addOnCompleteListener { it ->
                            if (it.isSuccessful) {
                                // This is how you get the download URL
                                storage.child("users").child(uid).child("stories").child(imageUri.lastPathSegment!!).downloadUrl.addOnSuccessListener {
                                    taskSource.setResult(it)
                                }

                            } else {
                                taskSource.setException(it.exception!!)
                            }
                        }

            }

    override fun setUserStory(uid: String, downloadUri: Uri): Task<Unit> =
            database.child("stories").child(uid).push()
                    .setValue(downloadUri.toString()).toUnit()

    override fun checkStories(uid: String): LiveData<Story?> =
            FirebaseLiveData(database.child("story-user").child(uid).orderByKey().limitToLast(1)).map {
                it.asStory()
            }


    override fun setUserImage(uid: String, downloadUri: Uri): Task<Unit> =
            database.child("images").child(uid).push()
                    .setValue(downloadUri.toString()).toUnit()

    override fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri> =
            task { taskSource ->
                storage.child("users").child(uid).child("images")
                        .child(imageUri.lastPathSegment!!).putFile(imageUri).addOnCompleteListener { it ->
                            if (it.isSuccessful) {
                                // This is how you get the download URL
                                storage.child("users").child(uid).child("images").child(imageUri.lastPathSegment!!).downloadUrl.addOnSuccessListener {
                                    taskSource.setResult(it)
                                }

                            } else {
                                taskSource.setException(it.exception!!)
                            }
                        }

            }

    override fun createUser(user: User, password: String): Task<Unit> =
            auth.createUserWithEmailAndPassword(user.email, password).onSuccessTask {
                database.child("users").child(it!!.user!!.uid).setValue(user)
            }.toUnit()

    override fun isUserExistsForEmail(email: String): Task<Boolean> =
            auth.fetchSignInMethodsForEmail(email).onSuccessTask {
                val signInMethods = it?.signInMethods ?: emptyList<String>()
                Tasks.forResult(signInMethods.isNotEmpty())
            }

    override fun getImages(uid: String): LiveData<List<String>> =
            FirebaseLiveData(database.child("images").child(uid)).map {
                it.children.map { it.getValue(String::class.java)!! }
            }


    override fun getUsers(): LiveData<List<User>> =
            database.child("users").liveData().map {
                it.children.map { it.asUser()!! }
            }

    override fun addFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowsRef(fromUid, toUid).setValue(true).toUnit()
                    .addOnSuccessListener {
                        EventBus.publish(Event.CreateFollow(fromUid, toUid))
                    }

    override fun deleteFollow(fromUid: String, toUid: String): Task<Unit> =
            getFollowsRef(fromUid, toUid).removeValue().toUnit()

    override fun addFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).setValue(true).toUnit()

    override fun deleteFollower(fromUid: String, toUid: String): Task<Unit> =
            getFollowersRef(fromUid, toUid).removeValue().toUnit()

    private fun getFollowsRef(fromUid: String, toUid: String) =
            database.child("users").child(fromUid).child("follows").child(toUid)

    private fun getFollowersRef(fromUid: String, toUid: String) =
            database.child("users").child(toUid).child("followers").child(fromUid)

    override fun currentUid() = FirebaseAuth.getInstance().currentUser?.uid

    override fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> {
        val updatesMap = mutableMapOf<String, Any?>()
        if (newUser.name != currentUser.name) updatesMap["name"] = newUser.name
        if (newUser.username != currentUser.username) updatesMap["username"] = newUser.username
        if (newUser.website != currentUser.website) updatesMap["website"] = newUser.website
        if (newUser.bio != currentUser.bio) updatesMap["bio"] = newUser.bio
        if (newUser.email != currentUser.email) updatesMap["email"] = newUser.email
        if (newUser.phone != currentUser.phone) updatesMap["phone"] = newUser.phone

        return database.child("users").child(currentUid()!!).updateChildren(updatesMap).toUnit()
    }

    override fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            val credential = EmailAuthProvider.getCredential(currentEmail, password)
            currentUser.reauthenticate(credential).onSuccessTask {
                currentUser.updateEmail(newEmail)
            }.toUnit()
        } else {
            Tasks.forException(IllegalStateException("User is not authenticated"))
        }
    }

    override fun uploadUserPhoto(localImage: Uri): Task<Uri> =
            task { taskSource ->
                storage.child("users/${currentUid()!!}/photo").putFile(localImage)
                        .addOnCompleteListener { it ->
                            if (it.isSuccessful) {
                                // This is how you get the download URL
                                storage.child("users/${currentUid()!!}/photo").downloadUrl.addOnSuccessListener {
                                    taskSource.setResult(it)
                                }

                            } else {
                                taskSource.setException(it.exception!!)
                            }
                        }
            }

    //Todo also update feedpost photo
    override fun updateUserPhoto(downloadUrl: Uri): Task<Unit> =
            database.child("users/${currentUid()!!}/photo").setValue(downloadUrl.toString()).toUnit()

    override fun getUser(): LiveData<User> = getUser(currentUid()!!)

    override fun getUser(uid: String): LiveData<User> =
            database.child("users").child(uid).liveData().map {
                it.asUser()!!
            }

    private fun DataSnapshot.asUser(): User? =
            getValue(User::class.java)?.copy(uid = key!!)

    private fun DataSnapshot.asStory(): Story? =
            getValue(Story::class.java)?.copy(id = key!!)
}