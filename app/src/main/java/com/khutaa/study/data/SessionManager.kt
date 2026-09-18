package com.khutaa.study.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Tracks whether a focus session is currently active and when it ends.
 * Backed by SharedPreferences so both the UI (MainActivity) and the
 * AppBlockerAccessibilityService -- which run in the same app process --
 * can read/write it without needing a full IPC layer.
 */
object SessionManager {
    private const val PREFS = "khutaa_session"
    private const val KEY_ACTIVE = "active"
    private const val KEY_END_AT = "end_at_millis"
    private const val KEY_SUBJECT = "subject"

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun startSession(context: Context, subject: String, durationMinutes: Int) {
        val endAt = System.currentTimeMillis() + durationMinutes * 60_000L
        prefs(context).edit()
            .putBoolean(KEY_ACTIVE, true)
            .putLong(KEY_END_AT, endAt)
            .putString(KEY_SUBJECT, subject)
            .apply()
    }

    fun endSession(context: Context) {
        prefs(context).edit().putBoolean(KEY_ACTIVE, false).apply()
    }

    fun isActive(context: Context): Boolean {
        val p = prefs(context)
        if (!p.getBoolean(KEY_ACTIVE, false)) return false
        val endAt = p.getLong(KEY_END_AT, 0L)
        if (System.currentTimeMillis() >= endAt) {
            // session naturally expired
            endSession(context)
            return false
        }
        return true
    }

    fun remainingMillis(context: Context): Long {
        val endAt = prefs(context).getLong(KEY_END_AT, 0L)
        return (endAt - System.currentTimeMillis()).coerceAtLeast(0L)
    }

    fun subject(context: Context): String =
        prefs(context).getString(KEY_SUBJECT, "") ?: ""
}
