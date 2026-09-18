package com.khutaa.study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.khutaa.study.data.*
import com.khutaa.study.ui.screens.*
import com.khutaa.study.ui.theme.KhutaaTheme
import kotlinx.coroutines.launch

private object Routes {
    const val WELCOME = "welcome"
    const val SIGNUP = "signup"
    const val DURATION = "duration"
    const val STUDY_TIME = "study_time"
    const val APPS = "apps"
    const val HOME = "home"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KhutaaTheme {
                KhutaaApp()
            }
        }
    }
}

@Composable
private fun KhutaaApp() {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    // Decide the start destination once, based on whether onboarding was
    // already completed on a previous launch (fully local check).
    var startDestination by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val settings = db.userSettingsDao().get()
        startDestination = if (settings?.onboardingComplete == true) Routes.HOME else Routes.WELCOME
    }

    val resolvedStart = startDestination ?: return // simple loading gate

    NavHost(navController = navController, startDestination = resolvedStart) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onStart = { navController.navigate(Routes.SIGNUP) },
                onLogin = { navController.navigate(Routes.SIGNUP) }
            )
        }

        composable(Routes.SIGNUP) {
            SignupScreen(
                initialName = "",
                onNext = { name ->
                    scope.launch {
                        val current = db.userSettingsDao().get() ?: UserSettingsEntity()
                        db.userSettingsDao().upsert(current.copy(fullName = name))
                        navController.navigate(Routes.DURATION)
                    }
                }
            )
        }

        composable(Routes.DURATION) {
            DurationScreen(
                initialMinutes = 120,
                onNext = { minutes ->
                    scope.launch {
                        val current = db.userSettingsDao().get() ?: UserSettingsEntity()
                        db.userSettingsDao().upsert(current.copy(dailyGoalMinutes = minutes))
                        navController.navigate(Routes.STUDY_TIME)
                    }
                }
            )
        }

        composable(Routes.STUDY_TIME) {
            StudyTimeScreen(
                initialHour = 18,
                initialMinute = 0,
                onNext = { hour, minute ->
                    scope.launch {
                        val current = db.userSettingsDao().get() ?: UserSettingsEntity()
                        db.userSettingsDao().upsert(
                            current.copy(studyStartHour = hour, studyStartMinute = minute)
                        )
                        navController.navigate(Routes.APPS)
                    }
                }
            )
        }

        composable(Routes.APPS) {
            AppsSelectionScreen(
                initiallySelected = emptySet(),
                onFinish = { selectedPackages ->
                    scope.launch {
                        selectedPackages.forEach { pkg ->
                            db.blockedAppDao().upsert(
                                BlockedAppEntity(packageName = pkg, appName = pkg, isBlocked = true)
                            )
                        }
                        val current = db.userSettingsDao().get() ?: UserSettingsEntity()
                        db.userSettingsDao().upsert(current.copy(onboardingComplete = true))
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeRoute(navController = navController)
        }
    }
}

@Composable
private fun HomeRoute(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.get(context) }
    var uiState by remember {
        mutableStateOf(HomeUiState("", 120, 0, 0, 0))
    }

    // Refresh whenever this route is (re)composed, e.g. after finishing a session.
    LaunchedEffect(Unit) {
        val settings = db.userSettingsDao().get() ?: UserSettingsEntity()
        val blockedCount = db.blockedAppDao().getBlockedPackageNames().size
        uiState = HomeUiState(
            name = settings.fullName.ifBlank { "بطل المذاكرة" },
            dailyGoalMinutes = settings.dailyGoalMinutes,
            blockedAppsCount = blockedCount,
            sessionsToday = StatsManager.sessionsToday(context),
            minutesToday = StatsManager.minutesToday(context)
        )
    }

    HomeScreen(
        state = uiState,
        onStartSession = { durationMinutes, subject ->
            SessionManager.startSession(context, subject, durationMinutes)
            StatsManager.recordSession(context, durationMinutes)
        },
        onEditApps = { navController.navigate(Routes.APPS) }
    )
}
