package com.kou.seekmake.screens.editprofile

import android.net.Uri
import androidx.lifecycle.LiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.kou.seekmake.common.SingleLiveEvent
import com.kou.seekmake.data.firebase.UsersRepository
import com.kou.seekmake.models.Firebase.User
import com.kou.seekmake.screens.common.BaseViewModel

class EditProfileViewModel(onFailureListener: OnFailureListener,
                           private val usersRepo: UsersRepository) : BaseViewModel(onFailureListener) {
    val user: LiveData<User> = usersRepo.getUser()
    private val _shareCompletedEvent = SingleLiveEvent<Unit>()
    val shareCompletedEvent = _shareCompletedEvent
    //todo update feedpost avatar too
    fun uploadAndSetUserPhoto(localImage: Uri): Task<Unit> =
            usersRepo.uploadUserPhoto(localImage).onSuccessTask { downloadUrl ->
                usersRepo.updateUserPhoto(downloadUrl!!).addOnSuccessListener {
                    shareCompletedEvent.call()
                }

            }.addOnFailureListener(onFailureListener)

    fun updateEmail(currentEmail: String, newEmail: String, password: String): Task<Unit> =
            usersRepo.updateEmail(currentEmail = currentEmail, newEmail = newEmail,
                    password = password)
                    .addOnFailureListener(onFailureListener)

    fun updateUserProfile(currentUser: User, newUser: User): Task<Unit> =
            usersRepo.updateUserProfile(currentUser = currentUser, newUser = newUser)
                    .addOnFailureListener(onFailureListener)
}
