package com.khutaa.study.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row table holding the user's onboarding choices.
 * id is always 0 -> there is only ever one settings row (local, offline).
 */
@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 0,
    val fullName: String = "",
    val dailyGoalMinutes: Int = 120,
    val studyStartHour: Int = 18,
    val studyStartMinute: Int = 0,
    val onboardingComplete: Boolean = false
)
