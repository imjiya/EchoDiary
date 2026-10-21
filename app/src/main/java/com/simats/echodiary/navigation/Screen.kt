package com.simats.echodiary.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object SecondOnboarding : Screen("second_onboarding")
    object ThirdOnboarding : Screen("third_onboarding")
    object FourthOnboarding : Screen("fourth_onboarding")
    object Login : Screen("login")
    object ResetPassword : Screen("reset_password")
    object CreateAccount : Screen("create_account")
    object Home : Screen("home")
    object AddActivity : Screen("add_activity")
    object VoiceRecording : Screen("voice_recording")
    object ReviewTranscript : Screen("review_transcript")
    object WriteDiaryEntry : Screen("write_diary_entry")
    object ActivityDetails : Screen("activity_details")
    object EditActivity : Screen("edit_activity")
    object AllActivities : Screen("all_activities")
    object Calendar : Screen("calendar")
    object ChooseCategory : Screen("choose_category")
    object SearchDiary : Screen("search_diary")
    object FilterActivities : Screen("filter_activities")
}
