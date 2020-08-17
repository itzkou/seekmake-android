package com.kou.seekmake.screens.common.SharedUtils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.kou.seekmake.screens.common.SharedUtils.Prefs.Companion.Email
import com.kou.seekmake.screens.common.SharedUtils.Prefs.Companion.FName
import com.kou.seekmake.screens.common.SharedUtils.Prefs.Companion.ID
import com.kou.seekmake.screens.common.SharedUtils.Prefs.Companion.LName
import com.kou.seekmake.screens.common.SharedUtils.Prefs.Companion.PASS
import com.kou.seekmake.screens.common.SharedUtils.Prefs.Companion.TOKEN

class PrefsManager {
    companion object {
        fun getPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        fun seToken(context: Context, token: String?) {
            val editor = getPreferences(context).edit()
            editor.putString(TOKEN, token)
            editor.apply()
        }

        fun geToken(context: Context): String? {
            return getPreferences(context).getString(TOKEN, null)


        }

        fun seID(context: Context, id: String?) {
            val editor = getPreferences(context).edit()
            editor.putString(ID, id)
            editor.apply()
        }

        fun geID(context: Context): String? {
            return getPreferences(context).getString(ID, null)


        }

        fun seFname(context: Context, fname: String?) {
            val editor = getPreferences(context).edit()
            editor.putString(FName, fname)
            editor.apply()
        }

        fun geFname(context: Context): String? {
            return getPreferences(context).getString(FName, "")
        }

        fun seLname(context: Context, lname: String?) {
            val editor = getPreferences(context).edit()
            editor.putString(LName, lname)
            editor.apply()
        }

        fun geLname(context: Context): String? {
            return getPreferences(context).getString(LName, "")
        }

        fun sePwd(context: Context, pwd: String?) {
            val editor = getPreferences(context).edit()
            editor.putString(PASS, pwd)
            editor.apply()
        }

        fun gePwd(context: Context): String? {
            return getPreferences(context).getString(PASS, "")
        }

        fun seMail(context: Context, mail: String?) {
            val editor = getPreferences(context).edit()
            editor.putString(Email, mail)
            editor.apply()
        }

        fun geMail(context: Context): String? {
            return getPreferences(context).getString(Email, "")
        }

    }


}