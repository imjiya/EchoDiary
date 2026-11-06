package com.simats.echodiary

import com.simats.echodiary.navigation.EchoNavController
import com.simats.echodiary.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EchoNavigationUnitTest {

    @Test
    fun testInitialState() {
        val navController = EchoNavController(Screen.Splash)
        assertEquals(Screen.Splash, navController.currentScreen)
        assertEquals(1, navController.backStack.size)
        assertFalse("At root splash, canPop should be false", navController.canPop)
    }

    @Test
    fun testNavigatePushesScreen() {
        val navController = EchoNavController(Screen.Home)
        navController.navigate(Screen.AllActivities)
        assertEquals(Screen.AllActivities, navController.currentScreen)
        assertEquals(2, navController.backStack.size)
        assertTrue(navController.canPop)

        navController.navigate(Screen.ActivityDetails)
        assertEquals(Screen.ActivityDetails, navController.currentScreen)
        assertEquals(3, navController.backStack.size)
    }

    @Test
    fun testNavigateAvoidsDuplicateConsecutivePushes() {
        val navController = EchoNavController(Screen.Home)
        navController.navigate(Screen.Calendar)
        assertEquals(2, navController.backStack.size)

        navController.navigate(Screen.Calendar)
        assertEquals(2, navController.backStack.size)
        assertEquals(Screen.Calendar, navController.currentScreen)
    }

    @Test
    fun testPopBackStackRemovesTopScreen() {
        val navController = EchoNavController(Screen.Home)
        navController.navigate(Screen.Settings)
        navController.navigate(Screen.Appearance)

        assertEquals(Screen.Appearance, navController.currentScreen)
        assertEquals(3, navController.backStack.size)

        val popped1 = navController.popBackStack()
        assertTrue(popped1)
        assertEquals(Screen.Settings, navController.currentScreen)
        assertEquals(2, navController.backStack.size)

        val popped2 = navController.popBackStack()
        assertTrue(popped2)
        assertEquals(Screen.Home, navController.currentScreen)
        assertEquals(1, navController.backStack.size)

        val popped3 = navController.popBackStack()
        assertFalse("Cannot pop root screen", popped3)
        assertEquals(Screen.Home, navController.currentScreen)
        assertEquals(1, navController.backStack.size)
    }

    @Test
    fun testPopBackStackToTargetScreen() {
        val navController = EchoNavController(Screen.Home)
        navController.navigate(Screen.AllActivities)
        navController.navigate(Screen.SearchDiary)
        navController.navigate(Screen.ActivityDetails)
        navController.navigate(Screen.EditActivity)

        assertEquals(5, navController.backStack.size)

        val popped = navController.popBackStack(Screen.AllActivities, inclusive = false)
        assertTrue(popped)
        assertEquals(Screen.AllActivities, navController.currentScreen)
        assertEquals(2, navController.backStack.size)
    }

    @Test
    fun testNavigateRootClearsBackStack() {
        val navController = EchoNavController(Screen.Onboarding)
        navController.navigate(Screen.SecondOnboarding)
        navController.navigate(Screen.Login)
        assertEquals(3, navController.backStack.size)

        navController.navigateRoot(Screen.Home)
        assertEquals(1, navController.backStack.size)
        assertEquals(Screen.Home, navController.currentScreen)
        assertFalse(navController.canPop)
    }

    @Test
    fun testReplaceTop() {
        val navController = EchoNavController(Screen.Home)
        navController.navigate(Screen.YourWeeklySummary)
        navController.navigate(Screen.CreatingYourMemory)
        assertEquals(Screen.CreatingYourMemory, navController.currentScreen)
        assertEquals(3, navController.backStack.size)

        navController.replaceTop(Screen.WeeklyMemory)
        assertEquals(Screen.WeeklyMemory, navController.currentScreen)
        assertEquals(3, navController.backStack.size)

        navController.popBackStack()
        assertEquals(Screen.YourWeeklySummary, navController.currentScreen)
    }

    @Test
    fun testNavigateTab() {
        val navController = EchoNavController(Screen.Home)
        navController.navigateTab(Screen.Calendar)
        assertEquals(Screen.Calendar, navController.currentScreen)
        assertEquals(listOf(Screen.Home, Screen.Calendar), navController.backStack.toList())

        navController.navigateTab(Screen.Memories)
        assertEquals(Screen.Memories, navController.currentScreen)
        assertEquals(listOf(Screen.Home, Screen.Memories), navController.backStack.toList())

        navController.navigateTab(Screen.Home)
        assertEquals(Screen.Home, navController.currentScreen)
        assertEquals(listOf(Screen.Home), navController.backStack.toList())
    }

    @Test
    fun testNavigateAndPopUpTo() {
        val navController = EchoNavController(Screen.Home)
        navController.navigate(Screen.AddActivity)
        navController.navigate(Screen.WriteDiaryEntry)
        assertEquals(3, navController.backStack.size)

        navController.navigateAndPopUpTo(Screen.ActivityDetails, popUpTo = Screen.Home, inclusive = false)
        assertEquals(listOf(Screen.Home, Screen.ActivityDetails), navController.backStack.toList())
        assertEquals(Screen.ActivityDetails, navController.currentScreen)

        navController.popBackStack()
        assertEquals(Screen.Home, navController.currentScreen)
    }
}
