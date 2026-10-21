package com.simats.echodiary.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object SecondOnboarding : Screen("second_onboarding")
    object ThirdOnboarding : Screen("third_onboarding")
    object Login : Screen("login")
    object Home : Screen("home")
}
