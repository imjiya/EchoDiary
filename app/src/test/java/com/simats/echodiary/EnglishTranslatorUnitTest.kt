package com.simats.echodiary

import com.simats.echodiary.data.util.EnglishTranslator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EnglishTranslatorUnitTest {

    @Test
    fun testPolishEnglishCapitalizationAndPunctuation() {
        val raw = "today i attended my lectures and worked on my project"
        val polished = EnglishTranslator.polishEnglish(raw)

        assertTrue(polished.startsWith("Today"))
        assertTrue(polished.contains(" I "))
        assertTrue(polished.endsWith("."))
    }

    @Test
    fun testPolishEnglishContractions() {
        val raw = "i dont know why im late but i cant help it"
        val polished = EnglishTranslator.polishEnglish(raw)

        assertTrue(polished.contains("I'm"))
        assertTrue(polished.contains("don't"))
        assertTrue(polished.contains("can't"))
    }

    @Test
    fun testTranslateToProperEnglishSpanish() = runBlocking {
        val raw = "hoy fue un gran dia"
        val result = EnglishTranslator.translateToProperEnglish(raw)

        assertNotNull(result)
        assertTrue(result.translatedEnglish.isNotBlank())
        assertTrue(
            result.translatedEnglish.lowercase().contains("today") ||
            result.translatedEnglish.lowercase().contains("day")
        )
    }

    @Test
    fun testTranslateToProperEnglishHindi() = runBlocking {
        val raw = "aaj maine bahut kaam kiya"
        val result = EnglishTranslator.translateToProperEnglish(raw)

        assertNotNull(result)
        assertTrue(result.translatedEnglish.isNotBlank())
        assertTrue(
            result.translatedEnglish.lowercase().contains("today") ||
            result.translatedEnglish.lowercase().contains("work") ||
            result.translatedEnglish.lowercase().contains("i")
        )
    }
}

