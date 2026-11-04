package com.simats.echodiary.data.util

import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

data class TranslationResult(
    val originalText: String,
    val translatedEnglish: String,
    val detectedLanguage: String = "auto",
    val isSuccess: Boolean = true
)

object EnglishTranslator {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    suspend fun translateToProperEnglish(input: String): TranslationResult = withContext(Dispatchers.IO) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return@withContext TranslationResult(
                originalText = input,
                translatedEnglish = "",
                detectedLanguage = "Unknown"
            )
        }

        try {
            val encodedQuery = URLEncoder.encode(trimmed, "UTF-8")
            val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=en&dt=t&q=$encodedQuery"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyString = response.body?.string()
                if (!bodyString.isNullOrBlank()) {
                    val rootElement = JsonParser.parseString(bodyString)
                    if (rootElement.isJsonArray) {
                        val rootArray = rootElement.asJsonArray
                        val sentencesArray = if (rootArray.size() > 0 && rootArray[0].isJsonArray) {
                            rootArray[0].asJsonArray
                        } else null

                        val detectedLangCode = if (rootArray.size() > 2 && rootArray[2].isJsonPrimitive) {
                            rootArray[2].asString
                        } else "en"

                        val sb = StringBuilder()
                        sentencesArray?.forEach { elem ->
                            if (elem.isJsonArray) {
                                val itemArray = elem.asJsonArray
                                if (itemArray.size() > 0 && itemArray[0].isJsonPrimitive) {
                                    sb.append(itemArray[0].asString)
                                }
                            }
                        }

                        val translatedRaw = sb.toString().ifBlank { trimmed }
                        val polishedEnglish = polishEnglish(translatedRaw)
                        val languageName = mapLanguageCodeToName(detectedLangCode)

                        return@withContext TranslationResult(
                            originalText = trimmed,
                            translatedEnglish = polishedEnglish,
                            detectedLanguage = languageName,
                            isSuccess = true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Network fallback to local grammar enhancement
        }

        val fallbackPolished = polishEnglish(trimmed)
        return@withContext TranslationResult(
            originalText = trimmed,
            translatedEnglish = fallbackPolished,
            detectedLanguage = "English",
            isSuccess = true
        )
    }

    fun polishEnglish(text: String): String {
        if (text.isBlank()) return ""
        var result = text.trim().replace("\\s+".toRegex(), " ")

        val contractions = listOf(
            "\\bi\\b" to "I",
            "\\bim\\b" to "I'm",
            "\\bi'm\\b" to "I'm",
            "\\bive\\b" to "I've",
            "\\bi've\\b" to "I've",
            "\\bid\\b" to "I'd",
            "\\bill\\b" to "I'll",
            "\\bdont\\b" to "don't",
            "\\bcant\\b" to "can't",
            "\\bwont\\b" to "won't",
            "\\bdidnt\\b" to "didn't",
            "\\bwasnt\\b" to "wasn't",
            "\\bisnt\\b" to "isn't",
            "\\barent\\b" to "aren't",
            "\\bcouldnt\\b" to "couldn't",
            "\\bshouldnt\\b" to "shouldn't",
            "\\bwouldnt\\b" to "wouldn't",
            "\\bgonna\\b" to "going to",
            "\\bwanna\\b" to "want to",
            "\\bgotta\\b" to "got to",
            "\\bkinda\\b" to "kind of"
        )

        for ((pattern, replacement) in contractions) {
            result = result.replace(Regex(pattern, RegexOption.IGNORE_CASE), replacement)
        }

        // Capitalize the first letter of each sentence
        result = result.replace(Regex("(^|[.!?]\\s+)([a-z])")) { match ->
            match.value.uppercase()
        }

        if (result.isNotEmpty() && !result.endsWith(".") && !result.endsWith("!") && !result.endsWith("?")) {
            result += "."
        }

        return result
    }

    private fun mapLanguageCodeToName(code: String): String {
        return when (code.lowercase()) {
            "en" -> "English"
            "hi" -> "Hindi"
            "ta" -> "Tamil"
            "te" -> "Telugu"
            "es" -> "Spanish"
            "fr" -> "French"
            "de" -> "German"
            "ja" -> "Japanese"
            "zh-cn", "zh" -> "Chinese"
            "ar" -> "Arabic"
            "bn" -> "Bengali"
            "mr" -> "Marathi"
            "gu" -> "Gujarati"
            "kn" -> "Kannada"
            "ml" -> "Malayalam"
            "pa" -> "Punjabi"
            "ur" -> "Urdu"
            "pt" -> "Portuguese"
            "ru" -> "Russian"
            "it" -> "Italian"
            "ko" -> "Korean"
            else -> code.uppercase()
        }
    }
}
