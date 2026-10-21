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
import com.simats.echodiary.ui.onboarding.OnboardingScreen
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
                            onNavigateToLogin = {
                                currentScreen = Screen.Login
                            }
                        )
                    }
                    is Screen.Login -> {
                        LoginScreen(
                            onNavigateBack = {
                                currentScreen = Screen.Onboarding
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
