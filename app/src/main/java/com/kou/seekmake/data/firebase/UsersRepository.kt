package com.kou.seekmake.data.firebase

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.Task
import com.kou.seekmake.models.Firebase.Story
import com.kou.seekmake.models.Firebase.User

interface UsersRepository {
    fun getUsers(): LiveData<List<User>>
    fun currentUid(): String?
    fun addFollow(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollow(fromUid: String, toUid: String): Task<Unit>
    fun addFollower(fromUid: String, toUid: String): Task<Unit>
    fun deleteFollower(fromUid: String, toUid: String): Task<Unit>
    fun getUser(): LiveData<User>
    fun uploadUserPhoto(localImage: Uri): Task<Uri>
    fun updateUserPhoto(downloadUrl: Uri): Task<Unit>
    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit>
    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit>
    fun getImages(uid: String): LiveData<List<String>>

    /** shit **/
    fun isUserExistsForEmail(email: String): Task<Boolean>
    fun createUser(user: User, password: String): Task<Unit>

    /** happens **/
    fun uploadUserImage(uid: String, imageUri: Uri): Task<Uri>
    fun setUserImage(uid: String, downloadUri: Uri): Task<Unit>
    fun uploadUserStory(uid: String, imageUri: Uri): Task<Uri>
    fun setUserStory(uid: String, downloadUri: Uri): Task<Unit>
    fun getUser(uid: String): LiveData<User>
    fun checkStories(uid: String): LiveData<Story?>
}