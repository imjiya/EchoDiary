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
import com.simats.echodiary.ui.activity.ActivityDetailsScreen
import com.simats.echodiary.ui.activity.AddActivityScreen
import com.simats.echodiary.ui.activity.AllActivitiesScreen
import com.simats.echodiary.ui.activity.EditActivityScreen
import com.simats.echodiary.ui.activity.ReviewTranscriptScreen
import com.simats.echodiary.ui.activity.VoiceRecordingScreen
import com.simats.echodiary.ui.activity.WriteDiaryEntryScreen
import com.simats.echodiary.ui.calendar.CalendarScreen
import com.simats.echodiary.ui.home.HomeScreen
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
                            onNavigateBack = {
                                currentScreen = Screen.AddActivity
                            },
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
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
                            onNavigateToActivityDetails = {
                                currentScreen = Screen.ActivityDetails
                            }
                        )
                    }
                    is Screen.AllActivities -> {
                        AllActivitiesScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Home
                            }
                        )
                    }
                    is Screen.Calendar -> {
                        CalendarScreen(
                            onNavigateToHome = {
                                currentScreen = Screen.Home
                            },
                            onNavigateToAllActivities = {
                                currentScreen = Screen.AllActivities
                            }
                        )
                    }
                }
            }
        }
    }
}
