package com.khutaa.study.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.khutaa.study.data.AppDatabase
import com.khutaa.study.data.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Watches which app comes to the foreground. If a focus session is active
 * and the foreground app is on the user's own commitment list, it sends
 * the user home and shows the in-app block screen instead of the
 * distracting app.
 *
 * Requires the user to enable this service once from
 * Settings > Accessibility > Khutaa, the same flow every similar
 * focus/blocker app on the Play Store uses.
 */
class AppBlockerAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var blockedPackages: Set<String> = emptySet()

    override fun onServiceConnected() {
        super.onServiceConnected()
        refreshBlockedList()
    }

    private fun refreshBlockedList() {
        scope.launch {
            val dao = AppDatabase.get(applicationContext).blockedAppDao()
            blockedPackages = dao.getBlockedPackageNames().toSet()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        if (pkg == packageName) return // our own app, ignore
        if (!SessionManager.isActive(applicationContext)) return
        if (pkg !in blockedPackages) return

        // Send the device home first so the blocked app isn't left underneath,
        // then show our own full-screen reminder on top.
        performGlobalAction(GLOBAL_ACTION_HOME)

        val overlayIntent = Intent(this, BlockOverlayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(BlockOverlayActivity.EXTRA_BLOCKED_APP, pkg)
        }
        startActivity(overlayIntent)
    }

    override fun onInterrupt() { /* no-op */ }
}
