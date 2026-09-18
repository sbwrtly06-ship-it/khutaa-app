package com.khutaa.study.data

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

data class InstalledApp(
    val packageName: String,
    val label: String
)

/**
 * Lists launchable, user-facing apps on the device so the person can pick
 * which ones to restrict. Entirely local (PackageManager query) -- no
 * network call, works fully offline.
 */
object InstalledAppsHelper {
    fun getLaunchableApps(context: Context): List<InstalledApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolved = pm.queryIntentActivities(intent, 0)
        return resolved
            .mapNotNull { info ->
                val appInfo: ApplicationInfo = info.activityInfo.applicationInfo
                val pkg = appInfo.packageName
                if (pkg == context.packageName) return@mapNotNull null
                val label = pm.getApplicationLabel(appInfo).toString()
                InstalledApp(packageName = pkg, label = label)
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
}
