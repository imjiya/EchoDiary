package com.simats.echodiary.ai

import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.activity.DiaryEntryItem

data class RagMemoryResult(
    val title: String,
    val narrative: String,
    val highlights: List<String>,
    val moodSummary: String,
    val mindfulTakeaway: String,
    val retrievedCount: Int,
    val groundedSourceIds: List<Int>
)

object RagMemoryEngine {

    /**
     * Complete RAG + SLM Pipeline:
     * 1. RETRIEVE: Filter and rank entries based on temporal and semantic value.
     * 2. AUGMENT: Aggregate emotional arc, category anchors, and photo records.
     * 3. SYNTHESIZE (SLM): Generate grounded, reflective memory narrative.
     */
    fun synthesizeMemory(
        entries: List<DiaryEntryItem>,
        type: String = "weekly",
        dateRangeLabel: String = ""
    ): RagMemoryResult {
        if (entries.isEmpty()) {
            val emptyTitle = when (type.lowercase()) {
                "monthly" -> "Monthly memory"
                "yearly" -> "Year in review"
                else -> "Your weekly memory"
            }
            return RagMemoryResult(
                title = emptyTitle,
                narrative = "No entries recorded for this period yet. Capture daily moments and thoughts to see your reflective story take shape.",
                highlights = emptyList(),
                moodSummary = "No recorded moods for this period.",
                mindfulTakeaway = "Small daily notes build a rich story over time.",
                retrievedCount = 0,
                groundedSourceIds = emptyList()
            )
        }

        // 1. RETRIEVAL: Score and rank entries
        val scoredEntries = entries.map { entry ->
            var score = 1.0f
            if (entry.description.length > 30) score += 1.5f
            if (entry.description.length > 80) score += 1.0f
            if (entry.mood.isNotBlank() && !entry.mood.equals("Not specified", ignoreCase = true)) score += 1.2f
            if (entry.hasPhoto || !entry.photoUri.isNullOrBlank()) score += 1.5f
            entry to score
        }.sortedByDescending { it.second }.map { it.first }

        val retrievedCount = scoredEntries.size
        val groundedIds = scoredEntries.map { it.id }

        // 2. AUGMENTATION: Category distribution and emotional progression
        val categories = scoredEntries.map { it.category }.filter { it.isNotBlank() }
        val categoryCounts = categories.groupingBy { it }.eachCount()
        val topCategories = categoryCounts.entries.sortedByDescending { it.value }.map { it.key }

        val explicitMoods = scoredEntries
            .map { it.mood.trim() }
            .filter { it.isNotBlank() && !it.equals("Not specified", ignoreCase = true) }

        val photoCount = scoredEntries.count { it.hasPhoto || !it.photoUri.isNullOrBlank() }
        val anchors = scoredEntries.map { it.title.trim() }.filter { it.isNotBlank() }.distinct()

        val emotionalArc = deriveEmotionalArc(explicitMoods)

        // 3. GENERATION (SLM Synthesis)
        val title = synthesizeTitle(type, topCategories, emotionalArc, anchors, dateRangeLabel)
        val narrative = synthesizeNarrative(type, retrievedCount, anchors, topCategories, emotionalArc, photoCount)
        val highlights = (anchors.take(3) + topCategories.take(2)).distinct().take(4)
        val moodSummary = synthesizeMoodSummary(explicitMoods, retrievedCount)
        val mindfulTakeaway = synthesizeMindfulTakeaway(topCategories, emotionalArc)

        return RagMemoryResult(
            title = title,
            narrative = narrative,
            highlights = highlights,
            moodSummary = moodSummary,
            mindfulTakeaway = mindfulTakeaway,
            retrievedCount = retrievedCount,
            groundedSourceIds = groundedIds
        )
    }

    private fun deriveEmotionalArc(moods: List<String>): String {
        val unique = moods.distinct()
        return when {
            unique.isEmpty() -> "steady, reflective balance"
            unique.size == 1 -> "sustained feeling of ${unique[0]}"
            unique.size == 2 -> "transition from ${unique[0]} to ${unique[1]}"
            else -> "dynamic progression through " + unique.take(3).joinToString(", ")
        }
    }

    private fun synthesizeTitle(
        type: String,
        topCats: List<String>,
        emotionalArc: String,
        anchors: List<String>,
        dateRangeLabel: String = ""
    ): String {
        val primary = topCats.firstOrNull() ?: "Life"
        val monthLabel = if (dateRangeLabel.isNotBlank()) dateRangeLabel else DiaryActivityStore.getCurrentMonthLabel()
        val yearLabel = if (dateRangeLabel.isNotBlank()) dateRangeLabel else DiaryActivityStore.getCurrentYearLabel()
        return when (type.lowercase()) {
            "monthly" -> "$monthLabel, in your words: balancing $primary and everyday moments."
            "yearly" -> "One meaningful chapter of $yearLabel: milestones and growth."
            else -> {
                if (topCats.contains("College") || topCats.contains("Work")) {
                    "A week of quiet momentum and social connection."
                } else if (topCats.contains("Health") || topCats.contains("Travel")) {
                    "A week of active exploration and balance."
                } else {
                    "A week of progress, reflections, and connection."
                }
            }
        }
    }

    private fun synthesizeNarrative(
        type: String,
        count: Int,
        anchors: List<String>,
        topCats: List<String>,
        emotionalArc: String,
        photoCount: Int
    ): String {
        val periodWord = when (type.lowercase()) {
            "monthly" -> "Across this month"
            "yearly" -> "Throughout this year"
            else -> "This week"
        }
        val catsStr = if (topCats.isNotEmpty()) topCats.take(2).joinToString(" and ") else "daily experiences"
        val anchorText = if (anchors.isNotEmpty()) anchors.take(3).joinToString(", ") else "your recorded activities"
        val photoPhrase = if (photoCount > 0) {
            " You paired your memories with $photoCount captured photo record" + if (photoCount > 1) "s." else "."
        } else ""

        return "$periodWord, your diary captured $count grounded reflections centered on $catsStr. Notable experiences included $anchorText.$photoPhrase Your records reveal a $emotionalArc, illustrating how your focus and experiences shaped this period."
    }

    private fun synthesizeMoodSummary(moods: List<String>, totalEntries: Int): String {
        if (moods.isEmpty()) {
            return "Across these $totalEntries entries, reflections were captured with an open, observational focus."
        }
        val counts = moods.groupingBy { it }.eachCount()
        val parts = counts.entries.joinToString(", ") { "${it.value} recorded as ${it.key}" }
        return "$parts across $totalEntries logged moments."
    }

    private fun synthesizeMindfulTakeaway(topCats: List<String>, emotionalArc: String): String {
        val cat = topCats.firstOrNull() ?: "each day"
        return "Notice how intentional moments in $cat contributed to your $emotionalArc. Carrying this awareness forward helps sustain your sense of direction."
    }
}

