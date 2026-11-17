package com.simats.echodiary

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.simats.echodiary.data.model.RegisterTokenRequest
import com.simats.echodiary.data.network.RetrofitClient
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.navigation.EchoNavController
import com.simats.echodiary.navigation.Screen
import com.simats.echodiary.navigation.rememberEchoNavController
import com.simats.echodiary.util.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.simats.echodiary.ui.activity.ActivityAndMoodScreen
import com.simats.echodiary.ui.activity.ActivityDetailsScreen
import com.simats.echodiary.ui.activity.AddActivityScreen
import com.simats.echodiary.ui.activity.AnalysisResultsScreen
import com.simats.echodiary.ui.activity.AllActivitiesScreen
import com.simats.echodiary.ui.activity.ChooseCategoryScreen
import com.simats.echodiary.ui.activity.CreatingYourMemoryScreen
import com.simats.echodiary.ui.activity.EditActivityScreen
import com.simats.echodiary.ui.activity.EditYourMemoryScreen
import com.simats.echodiary.ui.activity.MemoryDetailsScreen
import com.simats.echodiary.ui.activity.FilterActivitiesScreen
import com.simats.echodiary.ui.activity.MemoriesScreen
import com.simats.echodiary.ui.activity.MonthlyMemoryScreen
import com.simats.echodiary.ui.activity.YearlyMemoryScreen
import com.simats.echodiary.ui.activity.PhotoMemoriesScreen
import com.simats.echodiary.ui.activity.ReviewTranscriptScreen
import com.simats.echodiary.ui.activity.SearchDiaryScreen
import com.simats.echodiary.ui.activity.SelectedDateScreen
import com.simats.echodiary.ui.activity.VoiceRecordingScreen
import com.simats.echodiary.ui.activity.WriteDiaryEntryScreen
import com.simats.echodiary.ui.activity.WriteDiaryEntryViewModel
import com.simats.echodiary.ui.activity.YourWeeklySummaryScreen
import com.simats.echodiary.ui.activity.WeeklyMemoryScreen
import com.simats.echodiary.ui.calendar.CalendarScreen
import com.simats.echodiary.ui.home.HomeScreen
import com.simats.echodiary.ui.home.HomeViewModel
import com.simats.echodiary.ui.profile.AppearanceScreen
import com.simats.echodiary.ui.profile.DialogsAndFeedbackScreen
import com.simats.echodiary.ui.profile.EditProfileScreen
import com.simats.echodiary.ui.profile.EmptyStatesScreen
import com.simats.echodiary.ui.profile.HelpAndSupportScreen
import com.simats.echodiary.ui.profile.NotificationsScreen
import com.simats.echodiary.ui.profile.PrivacyAndDataScreen
import com.simats.echodiary.ui.profile.ProfileScreen
import com.simats.echodiary.ui.profile.ProfileViewModel
import com.simats.echodiary.ui.profile.SettingsScreen
import com.simats.echodiary.ui.profile.StorageScreen
import com.simats.echodiary.ui.login.LoginScreen
import com.simats.echodiary.ui.login.ResetPasswordScreen
import com.simats.echodiary.ui.onboarding.FourthOnboardingScreen
import com.simats.echodiary.ui.onboarding.OnboardingScreen
import com.simats.echodiary.ui.onboarding.SecondOnboardingScreen
import com.simats.echodiary.ui.onboarding.ThirdOnboardingScreen
import com.simats.echodiary.ui.register.CreateAccountScreen
import com.simats.echodiary.ui.splash.SplashScreen
import com.simats.echodiary.ui.theme.EchoDiaryTheme

class MainActivity : ComponentActivity() {

    private val pendingNotificationDestination = MutableStateFlow<Screen?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        getDestinationScreen(intent)?.let {
            pendingNotificationDestination.value = it
        }

        // Fetch Firebase FCM token & register with backend
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    if (!token.isNullOrBlank()) {
                        DiaryActivityStore.setFcmToken(token)
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                val api = RetrofitClient.getApi(applicationContext)
                                api.registerDeviceToken(RegisterTokenRequest(fcmToken = token))
                            } catch (_: Exception) {}
                        }
                    }
                }
            }
        } catch (_: Exception) {}

        // Cache initial entries from backend asynchronously without blocking UI
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                EchoDiaryApp.getEntryRepository().getEntries(forceRefresh = false)
            } catch (_: Exception) {}
        }

        setContent {
            val themeMode by DiaryActivityStore.themeMode.collectAsState()
            val themePalette by DiaryActivityStore.themePalette.collectAsState()
            val isDark = when (themeMode) {
                "Dark" -> true
                "Light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }
            EchoDiaryTheme(darkTheme = isDark, paletteName = themePalette) {
                val navController = rememberEchoNavController(Screen.Splash)
                val currentScreen = navController.currentScreen
                val pendingDestination by pendingNotificationDestination.collectAsState()

                LaunchedEffect(pendingDestination) {
                    pendingDestination?.let { target ->
                        navController.navigate(target)
                        pendingNotificationDestination.value = null
                    }
                }

                val todayFormatted = remember {
                    java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                }
                var recordedTranscript by remember { mutableStateOf("") }
                var entryTitle by remember { mutableStateOf("") }
                var entryDescription by remember { mutableStateOf("") }
                var entryDate by remember { mutableStateOf(todayFormatted) }
                var entryCategory by remember { mutableStateOf("College") }
                var entryMood by remember { mutableStateOf("Not specified") }
                var entryId by remember { mutableStateOf(0) }
                var entryPhotoUri by remember { mutableStateOf<String?>(null) }
                var entryHasPhoto by remember { mutableStateOf(false) }
                val writeDiaryViewModel = remember { WriteDiaryEntryViewModel() }
                val homeViewModel = remember { HomeViewModel() }
                val profileViewModel = remember { ProfileViewModel() }

                BackHandler(enabled = navController.canPop) {
                    navController.popBackStack()
                }

                when (currentScreen) {
                    is Screen.Splash -> {
                        SplashScreen(
                            onNavigateToOnboarding = {
                                if (EchoDiaryApp.getSessionManager().isLoggedIn()) {
                                    navController.navigateRoot(Screen.Home)
                                } else {
                                    navController.navigateRoot(Screen.Onboarding)
                                }
                            }
                        )
                    }
                    is Screen.Onboarding -> {
                        OnboardingScreen(
                            onNavigateToSecondOnboarding = {
                                navController.navigate(Screen.SecondOnboarding)
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login)
                            }
                        )
                    }
                    is Screen.SecondOnboarding -> {
                        SecondOnboardingScreen(
                            onNavigateToThirdOnboarding = {
                                navController.navigate(Screen.ThirdOnboarding)
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login)
                            }
                        )
                    }
                    is Screen.ThirdOnboarding -> {
                        ThirdOnboardingScreen(
                            onNavigateToFourthOnboarding = {
                                navController.navigate(Screen.FourthOnboarding)
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login)
                            }
                        )
                    }
                    is Screen.FourthOnboarding -> {
                        FourthOnboardingScreen(
                            onNavigateToCreateAccount = {
                                navController.navigate(Screen.CreateAccount)
                            }
                        )
                    }
                    is Screen.CreateAccount -> {
                        CreateAccountScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToLogin = {
                                navController.navigate(Screen.Login)
                            },
                            onNavigateToHome = {
                                navController.navigateRoot(Screen.Home)
                            }
                        )
                    }
                    is Screen.Login -> {
                        LoginScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToForgotPassword = {
                                navController.navigate(Screen.ResetPassword)
                            },
                            onNavigateToCreateAccount = {
                                navController.navigate(Screen.CreateAccount)
                            },
                            onNavigateToHome = {
                                navController.navigateRoot(Screen.Home)
                            }
                        )
                    }
                    is Screen.ResetPassword -> {
                        ResetPasswordScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.Home -> {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onNavigateToAddActivity = {
                                navController.navigate(Screen.AddActivity)
                            },
                            onNavigateToWriteDiary = {
                                navController.navigate(Screen.WriteDiaryEntry)
                            },
                            onNavigateToAllActivities = {
                                navController.navigate(Screen.AllActivities)
                            },
                            onNavigateToCalendar = {
                                navController.navigateTab(Screen.Calendar)
                            },
                            onNavigateToMemories = {
                                navController.navigateTab(Screen.Memories)
                            },
                            onNavigateToYourWeeklySummary = {
                                navController.navigate(Screen.YourWeeklySummary)
                            },
                            onNavigateToProfile = {
                                navController.navigateTab(Screen.Profile)
                            },
                            onNavigateToActivityDetails = { activityItem ->
                                if (activityItem != null) {
                                    entryTitle = activityItem.title
                                    entryDescription = activityItem.description
                                    entryDate = activityItem.date
                                    entryCategory = activityItem.category
                                    entryMood = activityItem.mood
                                    entryId = activityItem.id
                                    entryPhotoUri = activityItem.photoUri
                                    entryHasPhoto = activityItem.hasPhoto
                                    DiaryActivityStore.selectActivity(activityItem)
                                }
                                navController.navigate(Screen.ActivityDetails)
                            },
                            onNavigateToPhotoMemories = {
                                navController.navigate(Screen.PhotoMemories)
                            },
                            onNavigateToInsights = {
                                navController.navigate(Screen.ActivityAndMood)
                            },
                            onNavigateToChooseCategory = {
                                navController.navigate(Screen.ChooseCategory)
                            }
                        )
                    }
                    is Screen.AddActivity -> {
                        AddActivityScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToVoiceRecording = {
                                navController.navigate(Screen.VoiceRecording)
                            },
                            onNavigateToWriteDiaryEntry = {
                                navController.navigate(Screen.WriteDiaryEntry)
                            },
                            onNavigateToFilterActivities = {
                                navController.navigate(Screen.FilterActivities)
                            },
                            onNavigateToChooseCategory = {
                                navController.navigate(Screen.ChooseCategory)
                            }
                        )
                    }
                    is Screen.VoiceRecording -> {
                        VoiceRecordingScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToReviewTranscript = { transcript ->
                                recordedTranscript = transcript
                                navController.navigate(Screen.ReviewTranscript)
                            }
                        )
                    }
                    is Screen.ReviewTranscript -> {
                        ReviewTranscriptScreen(
                            initialTranscript = recordedTranscript,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToVoiceRecording = {
                                navController.popBackStack()
                            },
                            onNavigateToActivityDetails = { transcript ->
                                recordedTranscript = transcript
                                val item = DiaryActivityStore.selectedActivity.value
                                entryTitle = item?.title ?: "Voice Diary Entry"
                                entryDescription = transcript
                                entryCategory = item?.category ?: "Voice Diary"
                                entryMood = item?.mood ?: "Reflective"
                                entryId = item?.id ?: 0
                                entryPhotoUri = item?.photoUri
                                entryHasPhoto = item?.hasPhoto == true
                                navController.navigateAndPopUpTo(Screen.ActivityDetails, popUpTo = Screen.Home)
                            }
                        )
                    }
                    is Screen.WriteDiaryEntry -> {
                        WriteDiaryEntryScreen(
                            viewModel = writeDiaryViewModel,
                            initialDate = entryDate,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToActivityDetails = { title, description, date, category ->
                                val item = DiaryActivityStore.selectedActivity.value
                                entryTitle = title
                                entryDescription = description
                                entryDate = date
                                entryCategory = category
                                entryMood = item?.mood ?: writeDiaryViewModel.uiState.value.mood
                                entryId = item?.id ?: 0
                                entryPhotoUri = item?.photoUri ?: writeDiaryViewModel.uiState.value.photoUri
                                entryHasPhoto = item?.hasPhoto ?: writeDiaryViewModel.uiState.value.hasPhoto
                                navController.navigateAndPopUpTo(Screen.ActivityDetails, popUpTo = Screen.Home)
                            },
                            onNavigateToChooseCategory = {
                                navController.navigate(Screen.ChooseCategory)
                            },
                            onNavigateToCalendar = {
                                navController.navigate(Screen.Calendar)
                            }
                        )
                    }
                    is Screen.ActivityDetails -> {
                        val currentSelected = DiaryActivityStore.selectedActivity.value
                        val resolvedPhotoUri = currentSelected?.photoUri ?: entryPhotoUri
                        val resolvedHasPhoto = currentSelected?.hasPhoto == true || !resolvedPhotoUri.isNullOrBlank() || entryHasPhoto
                        ActivityDetailsScreen(
                            initialTitle = currentSelected?.title?.takeIf { it.isNotBlank() } ?: entryTitle,
                            initialDescription = currentSelected?.description?.takeIf { it.isNotBlank() } ?: entryDescription,
                            initialDate = currentSelected?.date?.takeIf { it.isNotBlank() } ?: entryDate,
                            initialCategory = currentSelected?.category?.takeIf { it.isNotBlank() } ?: entryCategory,
                            initialMood = currentSelected?.mood?.takeIf { it.isNotBlank() } ?: entryMood,
                            entryId = currentSelected?.id?.takeIf { it > 0 } ?: entryId,
                            initialPhotoUri = resolvedPhotoUri,
                            initialHasPhoto = resolvedHasPhoto,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToEdit = {
                                navController.navigate(Screen.EditActivity)
                            },
                            onNavigateToHome = {
                                if (!navController.popBackStack()) {
                                    navController.navigateTab(Screen.Home)
                                }
                            }
                        )
                    }
                    is Screen.EditActivity -> {
                        val currentSelected = DiaryActivityStore.selectedActivity.value
                        val resolvedPhotoUri = currentSelected?.photoUri ?: entryPhotoUri
                        val resolvedHasPhoto = currentSelected?.hasPhoto == true || !resolvedPhotoUri.isNullOrBlank() || entryHasPhoto
                        EditActivityScreen(
                            initialTitle = currentSelected?.title?.takeIf { it.isNotBlank() } ?: entryTitle,
                            initialDescription = currentSelected?.description?.takeIf { it.isNotBlank() } ?: entryDescription,
                            initialDate = currentSelected?.date?.takeIf { it.isNotBlank() } ?: entryDate,
                            initialCategory = currentSelected?.category?.takeIf { it.isNotBlank() } ?: entryCategory,
                            initialMood = currentSelected?.mood?.takeIf { it.isNotBlank() } ?: entryMood,
                            entryId = currentSelected?.id?.takeIf { it > 0 } ?: entryId,
                            initialPhotoUri = resolvedPhotoUri,
                            initialHasPhoto = resolvedHasPhoto,
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAllActivities = {
                                if (!navController.popBackStack(Screen.AllActivities)) {
                                    if (!navController.popBackStack(Screen.Home)) {
                                        navController.popBackStack()
                                    }
                                }
                            }
                        )
                    }
                    is Screen.AllActivities -> {
                        AllActivitiesScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToSearchDiary = {
                                navController.navigate(Screen.SearchDiary)
                            },
                            onNavigateToActivityDetails = { activityItem ->
                                entryTitle = activityItem.title
                                entryDescription = activityItem.description
                                entryDate = activityItem.date
                                entryCategory = activityItem.category
                                entryMood = activityItem.mood
                                entryId = activityItem.id
                                entryPhotoUri = activityItem.photoUri
                                entryHasPhoto = activityItem.hasPhoto
                                DiaryActivityStore.selectActivity(activityItem)
                                navController.navigate(Screen.ActivityDetails)
                            },
                            onNavigateToFilterActivities = {
                                navController.navigate(Screen.FilterActivities)
                            }
                        )
                    }
                    is Screen.Calendar -> {
                        CalendarScreen(
                            onNavigateToHome = {
                                navController.navigateTab(Screen.Home)
                            },
                            onNavigateToSelectedDate = {
                                navController.navigate(Screen.SelectedDate)
                            },
                            onNavigateToMemories = {
                                navController.navigateTab(Screen.Memories)
                            },
                            onNavigateToProfile = {
                                navController.navigateTab(Screen.Profile)
                            },
                            onNavigateToActivityDetails = {
                                navController.navigate(Screen.ActivityDetails)
                            },
                            onNavigateToWriteDiary = { selectedDate ->
                                entryDate = selectedDate
                                writeDiaryViewModel.onDateChanged(selectedDate)
                                navController.navigate(Screen.WriteDiaryEntry)
                            }
                        )
                    }
                    is Screen.ChooseCategory -> {
                        ChooseCategoryScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onCategorySelected = { cat ->
                                writeDiaryViewModel.onCategoryChanged(cat)
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.SearchDiary -> {
                        SearchDiaryScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToActivityDetails = {
                                navController.navigate(Screen.ActivityDetails)
                            }
                        )
                    }
                    is Screen.FilterActivities -> {
                        FilterActivitiesScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToActivityDetails = {
                                navController.navigate(Screen.ActivityDetails)
                            },
                            onNavigateToAllActivities = {
                                if (!navController.popBackStack(Screen.AllActivities)) {
                                    navController.navigate(Screen.AllActivities)
                                }
                            }
                        )
                    }
                    is Screen.SelectedDate -> {
                        SelectedDateScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToActivityDetails = {
                                navController.navigate(Screen.ActivityDetails)
                            },
                            onNavigateToAddEntry = {
                                navController.navigate(Screen.AddActivity)
                            }
                        )
                    }
                    is Screen.Memories -> {
                        MemoriesScreen(
                            onNavigateToHome = {
                                navController.navigateTab(Screen.Home)
                            },
                            onNavigateToCalendar = {
                                navController.navigateTab(Screen.Calendar)
                            },
                            onNavigateToAllActivities = {
                                navController.navigate(Screen.AllActivities)
                            },
                            onNavigateToYourWeeklySummary = {
                                navController.navigate(Screen.YourWeeklySummary)
                            },
                            onNavigateToMonthlyMemory = {
                                navController.navigate(Screen.MonthlyMemory)
                            },
                            onNavigateToYearlyMemory = {
                                navController.navigate(Screen.YearlyMemory)
                            },
                            onNavigateToPhotoMemories = {
                                navController.navigate(Screen.PhotoMemories)
                            },
                            onNavigateToProfile = {
                                navController.navigateTab(Screen.Profile)
                            }
                        )
                    }
                    is Screen.YourWeeklySummary -> {
                        YourWeeklySummaryScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAllActivities = {
                                navController.navigate(Screen.AllActivities)
                            },
                            onNavigateToCreatingMemory = {
                                navController.navigate(Screen.CreatingYourMemory)
                            },
                            onNavigateToAnalysisResults = {
                                navController.navigate(Screen.AnalysisResults)
                            }
                        )
                    }
                    is Screen.CreatingYourMemory -> {
                        CreatingYourMemoryScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToMemory = { memoryType ->
                                val target = when (memoryType.lowercase(java.util.Locale.ROOT)) {
                                    "monthly" -> Screen.MonthlyMemory
                                    "yearly" -> Screen.YearlyMemory
                                    else -> Screen.WeeklyMemory
                                }
                                navController.replaceTop(target)
                            }
                        )
                    }
                    is Screen.WeeklyMemory -> {
                        WeeklyMemoryScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToMemories = {
                                navController.navigateTab(Screen.Memories)
                            },
                            onNavigateToAllActivities = {
                                navController.navigate(Screen.AllActivities)
                            },
                            onNavigateToActivityAndMood = {
                                navController.navigate(Screen.ActivityAndMood)
                            },
                            onNavigateToEditYourMemory = {
                                navController.navigate(Screen.EditYourMemory)
                            },
                            onNavigateToMemoryDetails = {
                                navController.navigate(Screen.MemoryDetails)
                            }
                        )
                    }
                    is Screen.EditYourMemory -> {
                        EditYourMemoryScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.MemoryDetails -> {
                        MemoryDetailsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToActivityDetails = {
                                navController.navigate(Screen.ActivityDetails)
                            }
                        )
                    }
                    is Screen.AnalysisResults -> {
                        AnalysisResultsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToWeeklyMemory = {
                                navController.navigate(Screen.WeeklyMemory)
                            },
                            onNavigateToActivityDetails = {
                                navController.navigate(Screen.ActivityDetails)
                            }
                        )
                    }
                    is Screen.MonthlyMemory -> {
                        MonthlyMemoryScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAllActivities = {
                                navController.navigate(Screen.AllActivities)
                            },
                            onNavigateToActivityDetails = {
                                navController.navigate(Screen.ActivityDetails)
                            },
                            onNavigateToCreatingMemory = {
                                navController.navigate(Screen.CreatingYourMemory)
                            }
                        )
                    }
                    is Screen.YearlyMemory -> {
                        YearlyMemoryScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAllActivities = {
                                navController.navigate(Screen.AllActivities)
                            },
                            onNavigateToActivityDetails = {
                                navController.navigate(Screen.ActivityDetails)
                            },
                            onNavigateToCreatingMemory = {
                                navController.navigate(Screen.CreatingYourMemory)
                            }
                        )
                    }
                    is Screen.PhotoMemories -> {
                        PhotoMemoriesScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToActivityDetails = { selectedItem ->
                                val itemToOpen = selectedItem ?: DiaryActivityStore.selectedActivity.value
                                if (itemToOpen != null) {
                                    entryTitle = itemToOpen.title
                                    entryDescription = itemToOpen.description
                                    entryDate = itemToOpen.date
                                    entryCategory = itemToOpen.category
                                    entryMood = itemToOpen.mood
                                    entryId = itemToOpen.id
                                    entryPhotoUri = itemToOpen.photoUri
                                    entryHasPhoto = itemToOpen.hasPhoto || !itemToOpen.photoUri.isNullOrBlank()
                                    DiaryActivityStore.selectActivity(itemToOpen)
                                }
                                navController.navigate(Screen.ActivityDetails)
                            }
                        )
                    }
                    is Screen.ActivityAndMood -> {
                        ActivityAndMoodScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToActivityDetails = {
                                val item = DiaryActivityStore.selectedActivity.value
                                if (item != null) {
                                    entryTitle = item.title
                                    entryDescription = item.description
                                    entryDate = item.date
                                    entryCategory = item.category
                                    entryMood = item.mood
                                    entryId = item.id
                                    entryPhotoUri = item.photoUri
                                    entryHasPhoto = item.hasPhoto
                                }
                                navController.navigate(Screen.ActivityDetails)
                            }
                        )
                    }
                    is Screen.Profile -> {
                        ProfileScreen(
                            viewModel = profileViewModel,
                            onNavigateToHome = {
                                navController.navigateTab(Screen.Home)
                            },
                            onNavigateToCalendar = {
                                navController.navigateTab(Screen.Calendar)
                            },
                            onNavigateToMemories = {
                                navController.navigateTab(Screen.Memories)
                            },
                            onNavigateToEditProfile = {
                                navController.navigate(Screen.EditProfile)
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.Settings)
                            },
                            onNavigateToInsights = {
                                navController.navigate(Screen.ActivityAndMood)
                            }
                        )
                    }
                    is Screen.EditProfile -> {
                        EditProfileScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.Settings -> {
                        SettingsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAccount = {
                                navController.navigate(Screen.EditProfile)
                            },
                            onNavigateToAppearance = {
                                navController.navigate(Screen.Appearance)
                            },
                            onNavigateToPrivacyAndData = {
                                navController.navigate(Screen.PrivacyAndData)
                            },
                            onNavigateToNotifications = {
                                navController.navigate(Screen.Notifications)
                            },
                            onNavigateToStorage = {
                                navController.navigate(Screen.Storage)
                            },
                            onNavigateToHelpAndSupport = {
                                navController.navigate(Screen.HelpAndSupport)
                            },
                            onNavigateToLogin = {
                                navController.navigateRoot(Screen.Login)
                            }
                        )
                    }
                    is Screen.Appearance -> {
                        AppearanceScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.DialogsAndFeedback -> {
                        DialogsAndFeedbackScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.Notifications -> {
                        NotificationsScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.PrivacyAndData -> {
                        PrivacyAndDataScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToEmptyStates = {
                                navController.navigate(Screen.EmptyStates)
                            }
                        )
                    }
                    is Screen.Storage -> {
                        StorageScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.HelpAndSupport -> {
                        HelpAndSupportScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    is Screen.EmptyStates -> {
                        EmptyStatesScreen(
                            onNavigateBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAddEntry = {
                                navController.navigate(Screen.AddActivity)
                            },
                            onNavigateToAllActivities = {
                                navController.navigate(Screen.AllActivities)
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        getDestinationScreen(intent)?.let {
            pendingNotificationDestination.value = it
        }
    }

    private fun getDestinationScreen(intent: Intent?): Screen? {
        val dest = intent?.getStringExtra(NotificationHelper.EXTRA_NAVIGATE_TO)
        return when (dest) {
            NotificationHelper.DESTINATION_WRITE_ENTRY -> Screen.WriteDiaryEntry
            NotificationHelper.DESTINATION_VOICE_RECORD -> Screen.VoiceRecording
            NotificationHelper.DESTINATION_PHOTO_MEMORIES -> Screen.PhotoMemories
            NotificationHelper.DESTINATION_HOME -> Screen.Home
            else -> null
        }
    }
}
