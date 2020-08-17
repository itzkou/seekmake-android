package com.kou.seekmake.screens.profilesettings

import com.google.android.gms.tasks.OnFailureListener
import com.kou.seekmake.common.AuthManager
import com.kou.seekmake.screens.common.BaseViewModel

class ProfileSettingsViewModel(private val authManager: AuthManager,
                               onFailureListener: OnFailureListener) :
        BaseViewModel(onFailureListener),
        AuthManager by authManager