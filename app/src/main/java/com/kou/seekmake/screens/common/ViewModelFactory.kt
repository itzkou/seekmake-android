package com.kou.seekmake.screens.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.data.firebase.StoryRepository
import com.kou.seekmake.screens.SeekMakeApp
import com.kou.seekmake.screens.addfriends.AddFriendsViewModel
import com.kou.seekmake.screens.comments.CommentsViewModel
import com.kou.seekmake.screens.editprofile.EditProfileViewModel
import com.kou.seekmake.screens.home.HomeViewModel
import com.kou.seekmake.screens.login.LoginViewModel
import com.kou.seekmake.screens.notifications.NotificationsViewModel
import com.kou.seekmake.screens.order.FileLoadingViewModel
import com.kou.seekmake.screens.profile.ProfileViewModel
import com.kou.seekmake.screens.profile_other.OtherViewModel
import com.kou.seekmake.screens.profilesettings.ProfileSettingsViewModel
import com.kou.seekmake.screens.register.RegisterViewModel
import com.kou.seekmake.screens.register.SeekRegisterViewModel
import com.kou.seekmake.screens.search.SearchViewModel
import com.kou.seekmake.screens.share.ShareViewModel
import com.kou.seekmake.screens.stories.StoryViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val app: SeekMakeApp,
                       private val commonViewModel: CommonViewModel,
                       private val onFailureListener: OnFailureListener) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val usersRepo = app.usersRepo
        val feedPostsRepo = app.feedPostsRepo
        val authManager = app.authManager
        val notificationsRepo = app.notificationsRepo
        val searchRepo = app.searchRepo
        val seekMakeRepo = app.seekMakeRepo
        val storyRepo: StoryRepository = app.storyRepo

        if (modelClass.isAssignableFrom(AddFriendsViewModel::class.java)) {
            return AddFriendsViewModel(onFailureListener, usersRepo, feedPostsRepo) as T
        } else if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(onFailureListener, usersRepo) as T
        } else if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(onFailureListener, feedPostsRepo, usersRepo) as T
        } else if (modelClass.isAssignableFrom(ProfileSettingsViewModel::class.java)) {
            return ProfileSettingsViewModel(authManager, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(seekMakeRepo, usersRepo, authManager, app, commonViewModel, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(usersRepo, seekMakeRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(commonViewModel, app, onFailureListener, usersRepo) as T
        } else if (modelClass.isAssignableFrom(ShareViewModel::class.java)) {
            return ShareViewModel(feedPostsRepo, usersRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(CommentsViewModel::class.java)) {
            return CommentsViewModel(feedPostsRepo, usersRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(NotificationsViewModel::class.java)) {
            return NotificationsViewModel(notificationsRepo, storyRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(searchRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(SeekRegisterViewModel::class.java)) {
            return SeekRegisterViewModel(seekMakeRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            return StoryViewModel(storyRepo, usersRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(FileLoadingViewModel::class.java)) {
            return FileLoadingViewModel(seekMakeRepo, onFailureListener) as T
        } else if (modelClass.isAssignableFrom(OtherViewModel::class.java)) {
            return OtherViewModel(usersRepo, feedPostsRepo, onFailureListener) as T
        } else {
            error("Unknown view model class $modelClass")
        }
    }
}