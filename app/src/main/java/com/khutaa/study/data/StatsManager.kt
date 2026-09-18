package com.khutaa.study.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Tracks today's completed-session count and focused minutes, all local. */
object StatsManager {
    private const val PREFS = "khutaa_stats"
    private val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private fun todayKey() = dayFormat.format(Date())

    fun recordSession(context: Context, minutes: Int) {
        val p = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val day = todayKey()
        val sessions = p.getInt("${day}_sessions", 0) + 1
        val mins = p.getInt("${day}_minutes", 0) + minutes
        p.edit()
            .putInt("${day}_sessions", sessions)
            .putInt("${day}_minutes", mins)
            .apply()
    }

    fun sessionsToday(context: Context): Int {
        val p = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return p.getInt("${todayKey()}_sessions", 0)
    }

    fun minutesToday(context: Context): Int {
        val p = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return p.getInt("${todayKey()}_minutes", 0)
    }
}
