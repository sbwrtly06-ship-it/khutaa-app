package com.khutaa.study.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One row per installed app the user chose to restrict during focus sessions. */
@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val isBlocked: Boolean = true
)
