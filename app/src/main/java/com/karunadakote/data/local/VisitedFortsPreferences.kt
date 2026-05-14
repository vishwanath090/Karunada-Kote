package com.karunadakote.data.local

import android.content.Context
import android.content.SharedPreferences

class VisitedFortsPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun markVisited(fortId: Int) {
        prefs.edit().putBoolean(keyFor(fortId), true).apply()
    }

    fun isVisited(fortId: Int): Boolean {
        return prefs.getBoolean(keyFor(fortId), false)
    }

    fun getVisitedIds(): Set<Int> {
        return prefs.all.keys
            .filter { it.startsWith(KEY_PREFIX) }
            .mapNotNull { it.removePrefix(KEY_PREFIX).toIntOrNull() }
            .toSet()
    }

    private fun keyFor(fortId: Int): String = "$KEY_PREFIX$fortId"

    companion object {
        private const val PREFS_NAME = "karunada_kote_prefs"
        private const val KEY_PREFIX = "visited_fort_"
    }
}
