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
import com.simats.echodiary.ui.home.HomeScreen
import com.simats.echodiary.ui.login.LoginScreen
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
                                /********* FIXED ROUTING *********/
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
                            onNavigateToHome = {
                                currentScreen = Screen.Home
                            }
                        )
                    }
                    is Screen.Home -> {
                        HomeScreen()
                    }
                }
            }
        }
    }
}
