package com.simats.echodiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.simats.echodiary.navigation.Screen
import com.simats.echodiary.ui.activity.ActivityAndMoodScreen
import com.simats.echodiary.ui.activity.ActivityDetailsScreen
import com.simats.echodiary.ui.activity.AddActivityScreen
import com.simats.echodiary.ui.activity.AnalysisResultsScreen
import com.simats.echodiary.ui.activity.AllActivitiesScreen
import com.simats.echodiary.ui.activity.ChooseCategoryScreen
import com.simats.echodiary.ui.activity.CreatingYourMemoryScreen
import com.simats.echodiary.ui.activity.EditActivityScreen
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
import com.simats.echodiary.ui.profile.EditProfileScreen
import com.simats.echodiary.ui.profile.ProfileScreen
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchoDiaryTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                val writeDiaryViewModel = remember { WriteDiaryEntryViewModel() }

                when (currentScreen) {
                    is Screen.Splash -> {
                        SplashScreen(
                            onNavigateToOnboarding = {
                                currentScreen = Screen.Onboarding
                            }
                        )
                    }
                    is Screen.Onboarding -> {
                        OnboardingScreen(
                            onNavigateToSecondOnboarding = {
                                currentScreen = Screen.SecondOnboarding
                            },
                            onNavigateToLogin = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    is Screen.SecondOnboarding -> {
                        SecondOnboardingScreen(
                            onNavigateToThirdOnboarding = {
                                currentScreen = Screen.ThirdOnboarding
                            },
                            onNavigateToLogin = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    is Screen.ThirdOnboarding -> {
                        ThirdOnboardingScreen(
                            onNavigateToFourthOnboarding = {
                                currentScreen = Screen.FourthOnboarding
                            },
                            onNavigateToLogin = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    is Screen.FourthOnboarding -> {
                        FourthOnboardingScreen(
                            onNavigateToCreateAccount = {
                                currentScreen = Screen.CreateAccount
                            }
                        )
                    }
                    is Screen.CreateAccount -> {
                        CreateAccountScreen(
                            onNavigateBack = {
                                currentScreen = Screen.FourthOnboarding
                            },
                            onNavigateToLogin = {
                                currentScreen = Screen.Login
                            },
                            onNavigateToHome = {
                                currentScreen = Screen.Home
                            }
                        )
                    }
                    is Screen.Login -> {
                        LoginScreen(
                            onNavigateBack = {
                                currentScreen = Screen.CreateAccount
                            },
                            onNavigateToForgotPassword = {
                                currentScreen = Screen.ResetPassword
                            },
                            onNavigateToCreateAccount = {
                                currentScreen = Screen.CreateAccount
                            },
                            onNavigateToHome = {
                                currentScreen = Screen.Home
                            }
                        )
                    }
                    is Screen.ResetPassword -> {
                        ResetPasswordScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    is Screen.Home -> {
                        HomeScreen(
                            onNavigateToAddActivity = {
                                currentScreen = Screen.AddActivity
                            },
                            onNavigateToAllActivities = {
                                currentScreen = Screen.AllActivities
                            },
                            onNavigateToCalendar = {
                                currentScreen = Screen.Calendar
                            },
                            onNavigateToMemories = {
                                currentScreen = Screen.Memories
                            },
                            onNavigateToProfile = {
                                currentScreen = Screen.Profile
                            }
                        )
                    }
                    is Screen.AddActivity -> {
                        AddActivityScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Home
                            },
                            onNavigateToVoiceRecording = {
                                currentScreen = Screen.VoiceRecording
                            },
                            onNavigateToWriteDiaryEntry = {
                                currentScreen = Screen.WriteDiaryEntry
                            },
                            onNavigateToFilterActivities = {
                                currentScreen = Screen.FilterActivities
                            }
                        )
                    }
                    is Screen.VoiceRecording -> {
                        VoiceRecordingScreen(
                            onNavigateBack = {
                                currentScreen = Screen.AddActivity
                            },
                            onNavigateToReviewTranscript = {
                                currentScreen = Screen.ReviewTranscript
                            }
                        )
                    }
                    is Screen.ReviewTranscript -> {
                        ReviewTranscriptScreen(
                            onNavigateBack = {
                                currentScreen = Screen.VoiceRecording
                            },
                            onNavigateToVoiceRecording = {
                                currentScreen = Screen.VoiceRecording
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            }
                        )
                    }
                    is Screen.WriteDiaryEntry -> {
                        WriteDiaryEntryScreen(
                            viewModel = writeDiaryViewModel,
                            onNavigateBack = {
                                currentScreen = Screen.AddActivity
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            },
                            onNavigateToChooseCategory = {
                                currentScreen = Screen.ChooseCategory
                            }
                        )
                    }
                    is Screen.ActivityDetails -> {
                        ActivityDetailsScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Home
                            },
                            onNavigateToEdit = {
                                currentScreen = Screen.EditActivity
                            },
                            onNavigateToHome = {
                                currentScreen = Screen.Home
                            }
                        )
                    }
                    is Screen.EditActivity -> {
                        EditActivityScreen(
                            onNavigateBack = {
                                currentScreen = Screen.ActivityDetails
                            },
                            onNavigateToAllActivities = {
                                currentScreen = Screen.AllActivities
                            }
                        )
                    }
                    is Screen.AllActivities -> {
                        AllActivitiesScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Home
                            },
                            onNavigateToSearchDiary = {
                                currentScreen = Screen.SearchDiary
                            }
                        )
                    }
                    is Screen.Calendar -> {
                        CalendarScreen(
                            onNavigateToHome = {
                                currentScreen = Screen.Home
                            },
                            onNavigateToSelectedDate = {
                                currentScreen = Screen.SelectedDate
                            },
                            onNavigateToMemories = {
                                currentScreen = Screen.Memories
                            },
                            onNavigateToProfile = {
                                currentScreen = Screen.Profile
                            }
                        )
                    }
                    is Screen.ChooseCategory -> {
                        ChooseCategoryScreen(
                            onNavigateBack = {
                                currentScreen = Screen.WriteDiaryEntry
                            },
                            onCategorySelected = { cat ->
                                writeDiaryViewModel.onCategoryChanged(cat)
                                currentScreen = Screen.WriteDiaryEntry
                            }
                        )
                    }
                    is Screen.SearchDiary -> {
                        SearchDiaryScreen(
                            onNavigateBack = {
                                currentScreen = Screen.AllActivities
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            }
                        )
                    }
                    is Screen.FilterActivities -> {
                        FilterActivitiesScreen(
                            onNavigateBack = {
                                currentScreen = Screen.AddActivity
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            }
                        )
                    }
                    is Screen.SelectedDate -> {
                        SelectedDateScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Calendar
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            },
                            onNavigateToAddEntry = {
                                currentScreen = Screen.AddActivity
                            }
                        )
                    }
                    is Screen.Memories -> {
                        MemoriesScreen(
                            onNavigateToHome = {
                                currentScreen = Screen.Home
                            },
                            onNavigateToCalendar = {
                                currentScreen = Screen.Calendar
                            },
                            onNavigateToAllActivities = {
                                currentScreen = Screen.AllActivities
                            },
                            onNavigateToYourWeeklySummary = {
                                currentScreen = Screen.YourWeeklySummary
                            },
                            onNavigateToMonthlyMemory = {
                                currentScreen = Screen.MonthlyMemory
                            },
                            onNavigateToYearlyMemory = {
                                currentScreen = Screen.YearlyMemory
                            },
                            onNavigateToPhotoMemories = {
                                currentScreen = Screen.PhotoMemories
                            },
                            onNavigateToProfile = {
                                currentScreen = Screen.Profile
                            }
                        )
                    }
                    is Screen.YourWeeklySummary -> {
                        YourWeeklySummaryScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Memories
                            },
                            onNavigateToAllActivities = {
                                currentScreen = Screen.AllActivities
                            },
                            onNavigateToCreatingMemory = {
                                currentScreen = Screen.CreatingYourMemory
                            },
                            onNavigateToAnalysisResults = {
                                currentScreen = Screen.AnalysisResults
                            }
                        )
                    }
                    is Screen.CreatingYourMemory -> {
                        CreatingYourMemoryScreen(
                            onNavigateBack = {
                                currentScreen = Screen.YourWeeklySummary
                            },
                            onNavigateToWeeklyMemory = {
                                currentScreen = Screen.WeeklyMemory
                            }
                        )
                    }
                    is Screen.WeeklyMemory -> {
                        WeeklyMemoryScreen(
                            onNavigateBack = {
                                currentScreen = Screen.CreatingYourMemory
                            },
                            onNavigateToMemories = {
                                currentScreen = Screen.Memories
                            },
                            onNavigateToAllActivities = {
                                currentScreen = Screen.AllActivities
                            },
                            onNavigateToActivityAndMood = {
                                currentScreen = Screen.ActivityAndMood
                            }
                        )
                    }
                    is Screen.AnalysisResults -> {
                        AnalysisResultsScreen(
                            onNavigateBack = {
                                currentScreen = Screen.YourWeeklySummary
                            },
                            onNavigateToWeeklyMemory = {
                                currentScreen = Screen.WeeklyMemory
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            }
                        )
                    }
                    is Screen.MonthlyMemory -> {
                        MonthlyMemoryScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Memories
                            },
                            onNavigateToAllActivities = {
                                currentScreen = Screen.AllActivities
                            }
                        )
                    }
                    is Screen.YearlyMemory -> {
                        YearlyMemoryScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Memories
                            },
                            onNavigateToAllActivities = {
                                currentScreen = Screen.AllActivities
                            }
                        )
                    }
                    is Screen.PhotoMemories -> {
                        PhotoMemoriesScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Memories
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            }
                        )
                    }
                    is Screen.ActivityAndMood -> {
                        ActivityAndMoodScreen(
                            onNavigateBack = {
                                currentScreen = Screen.WeeklyMemory
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            }
                        )
                    }
                    is Screen.Profile -> {
                        ProfileScreen(
                            onNavigateToHome = {
                                currentScreen = Screen.Home
                            },
                            onNavigateToCalendar = {
                                currentScreen = Screen.Calendar
                            },
                            onNavigateToMemories = {
                                currentScreen = Screen.Memories
                            },
                            onNavigateToEditProfile = {
                                currentScreen = Screen.EditProfile
                            },
                            onNavigateToInsights = {
                                currentScreen = Screen.ActivityAndMood
                            }
                        )
                    }
                    is Screen.EditProfile -> {
                        EditProfileScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Profile
                            }
                        )
                    }
                }
            }
        }
    }
}
