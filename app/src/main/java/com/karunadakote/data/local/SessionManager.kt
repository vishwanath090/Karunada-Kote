package com.karunadakote.data.local

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "karunadakote_session"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_JOIN_DATE = "join_date"
    }

    fun saveLogin(name: String, email: String, phone: String = "") {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PHONE, phone)
            if (!prefs.contains(KEY_JOIN_DATE)) {
                putString(KEY_JOIN_DATE, getCurrentDate())
            }
            apply()
        }
    }

    fun isLoggedIn(): Boolean =
        prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserName(): String =
        prefs.getString(KEY_USER_NAME, "Explorer") ?: "Explorer"

    fun getUserEmail(): String =
        prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun getUserPhone(): String =
        prefs.getString(KEY_USER_PHONE, "") ?: ""

    fun getJoinDate(): String =
        prefs.getString(KEY_JOIN_DATE, getCurrentDate()) ?: getCurrentDate()

    fun logout() {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
