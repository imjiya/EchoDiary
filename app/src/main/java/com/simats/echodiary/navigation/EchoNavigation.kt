package com.simats.echodiary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList

class EchoNavController(
    initialScreen: Screen = Screen.Splash
) {
    val backStack: SnapshotStateList<Screen> = mutableStateListOf(initialScreen)

    val currentScreen: Screen
        get() = backStack.lastOrNull() ?: Screen.Home

    val canPop: Boolean
        get() = backStack.size > 1 && currentScreen != Screen.Home

    fun navigate(screen: Screen) {
        if (currentScreen == screen) return
        backStack.add(screen)
    }

    fun popBackStack(): Boolean {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    fun popBackStack(targetScreen: Screen, inclusive: Boolean = false): Boolean {
        val targetIndex = backStack.indexOfLast { it == targetScreen }
        if (targetIndex != -1) {
            val fromIndex = if (inclusive) targetIndex else targetIndex + 1
            if (fromIndex < backStack.size) {
                while (backStack.size > fromIndex) {
                    backStack.removeAt(backStack.size - 1)
                }
                if (backStack.isEmpty()) {
                    backStack.add(Screen.Home)
                }
                return true
            }
        }
        return false
    }

    fun navigateRoot(screen: Screen) {
        backStack.clear()
        backStack.add(screen)
    }

    fun replaceTop(screen: Screen) {
        if (backStack.isNotEmpty()) {
            backStack.removeAt(backStack.size - 1)
        }
        backStack.add(screen)
    }

    fun navigateAndPopUpTo(screen: Screen, popUpTo: Screen, inclusive: Boolean = false) {
        val index = backStack.indexOfLast { it == popUpTo }
        if (index != -1) {
            val fromIndex = if (inclusive) index else index + 1
            while (backStack.size > fromIndex) {
                backStack.removeAt(backStack.size - 1)
            }
        }
        if (currentScreen != screen) {
            backStack.add(screen)
        }
    }

    fun navigateTab(screen: Screen) {
        if (currentScreen == screen) return
        if (screen == Screen.Home) {
            backStack.clear()
            backStack.add(Screen.Home)
        } else {
            backStack.clear()
            backStack.add(Screen.Home)
            backStack.add(screen)
        }
    }
}

@Composable
fun rememberEchoNavController(
    initialScreen: Screen = Screen.Splash
): EchoNavController {
    return remember { EchoNavController(initialScreen) }
}
