package com.apjake.ayuugamemanager.utils

import android.content.Context
import android.content.SharedPreferences
import com.apjake.ayuugamemanager.utils.Constants.KEY_SHARE_PREFERENCE

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(KEY_SHARE_PREFERENCE, Context.MODE_PRIVATE)

    companion object {
        const val USER_TOKEN_KEY = "AYuuUserToken"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN_KEY, token)
        editor.apply()
    }

    fun clearAuthToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN_KEY)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN_KEY, null)
    }
}