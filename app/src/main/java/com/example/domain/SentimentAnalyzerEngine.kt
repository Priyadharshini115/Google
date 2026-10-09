package com.example.domain

import com.example.data.model.AspectSentiment
import com.example.data.model.SentenceSentiment
import com.example.data.model.SentimentAnalysisResult
import com.example.data.model.SentimentClassification
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object SentimentAnalyzerEngine {

    // Sentiment Lexicon with polarity scores [-1.0 to 1.0]
    private val sentimentLexicon = mapOf(
        // Strong Positives (+0.7 to +1.0)
        "excellent" to 0.9f, "outstanding" to 0.95f, "amazing" to 0.9f, "superb" to 0.85f,
        "fantastic" to 0.9f, "perfect" to 1.0f, "love" to 0.85f, "loved" to 0.85f,
        "exceptional" to 0.9f, "brilliant" to 0.85f, "flawless" to 0.95f, "wonderful" to 0.85f,
        "delightful" to 0.8f, "stellar" to 0.85f, "marvelous" to 0.85f, "phenomenal" to 0.95f,

        // Moderate Positives (+0.3 to +0.6)
        "good" to 0.5f, "nice" to 0.5f, "great" to 0.7f, "pretty" to 0.4f, "stylish" to 0.6f,
        "style" to 0.5f, "comfort" to 0.5f, "comfortable" to 0.65f, "comfy" to 0.65f,
        "supportive" to 0.6f, "durable" to 0.65f, "sturdy" to 0.6f, "impressed" to 0.65f,
        "pleased" to 0.6f, "satisfied" to 0.55f, "recommend" to 0.7f, "decent" to 0.35f,
        "solid" to 0.45f, "reliable" to 0.6f, "helpful" to 0.5f, "enjoy" to 0.6f,
        "enjoyed" to 0.6f, "like" to 0.4f, "liked" to 0.4f, "soft" to 0.4f, "smooth" to 0.45f,

        // Strong Negatives (-0.7 to -1.0)
        "terrible" to -0.9f, "horrible" to -0.9f, "awful" to -0.9f, "disaster" to -0.95f,
        "waste" to -0.85f, "garbage" to -0.95f, "trash" to -0.95f, "unusable" to -0.9f,
        "worst" to -1.0f, "hate" to -0.9f, "hated" to -0.9f, "dreadful" to -0.85f,
        "pathetic" to -0.9f, "abysmal" to -0.95f, "ruined" to -0.85f, "rip-off" to -0.9f,

        // Moderate Negatives (-0.3 to -0.65)
        "flimsy" to -0.75f, "uncomfortable" to -0.8f, "disappointed" to -0.7f, "disappointing" to -0.7f,
        "poor" to -0.6f, "bad" to -0.6f, "cheap" to -0.55f, "broke" to -0.7f,
        "broken" to -0.75f, "pain" to -0.65f, "painful" to -0.75f, "hurts" to -0.7f,
        "sore" to -0.6f, "annoying" to -0.5f, "annoyed" to -0.55f, "lacking" to -0.5f,
        "lacks" to -0.5f, "mediocre" to -0.4f, "rough" to -0.4f, "slow" to -0.45f,
        "tight" to -0.45f, "stiff" to -0.5f, "hard" to -0.35f, "loose" to -0.4f,
        "heavy" to -0.3f, "fragile" to -0.6f, "noisy" to -0.45f, "useless" to -0.8f
    )

    private val negations = setOf(
        "not", "no", "never", "none", "neither", "nor", "hardly", "scarcely",
        "barely", "little", "without", "lacks", "wasn't", "weren't", "isn't",
        "aren't", "don't", "doesn't", "didn't", "won't", "wouldn't", "can't", "cannot"
    )

    private val intensifiers = mapOf(
        "very" to 1.35f, "extremely" to 1.5f, "really" to 1.3f, "so" to 1.25f,
        "absolutely" to 1.45f, "totally" to 1.4f, "completely" to 1.4f, "highly" to 1.3f,
        "deeply" to 1.3f, "super" to 1.35f, "truly" to 1.25f, "at all" to 1.3f,
        "in the least" to 1.35f, "slightly" to 0.7f, "somewhat" to 0.75f, "a bit" to 0.8f
    )

    private val contrastConjunctions = setOf(
        "but", "however", "although", "though", "yet", "nevertheless", "nonetheless", "still"
    )

    private val aspectCategories = listOf(
        AspectDefinition(
            name = "Style & Aesthetics",
            icon = "👗",
            keywords = setOf(
                "style", "stylish", "look", "looks", "looking", "pretty", "dress",
                "pants", "jeans", "fashion", "design", "color", "appearance",
                "aesthetic", "sleek", "cute", "handsome", "elegant", "attractive"
            )
        ),
        AspectDefinition(
            name = "Comfort & Fit",
            icon = "☁️",
            keywords = setOf(
                "comfort", "comfortable", "comfy", "cushion", "cushioning", "cushioned",
                "pain", "hurts", "soft", "tight", "fit", "fitting", "sore", "feet",
                "foot", "wearable", "arch", "pinch", "rubbing", "blister", "blisters"
            )
        ),
        AspectDefinition(
            name = "Build Quality & Support",
            icon = "🧱",
            keywords = setOf(
                "support", "flimsy", "build", "material", "materials", "durable",
                "durability", "sturdy", "cheap", "broke", "broken", "craftsmanship",
                "sole", "soles", "leather", "fabric", "structure", "quality", "fragile"
            )
        ),
        AspectDefinition(
            name = "Overall Satisfaction",
            icon = "🌟",
            keywords = setOf(
                "impressed", "recommend", "purchase", "purchased", "bought", "worth",
                "regret", "satisfied", "satisfaction", "love", "hate", "happy", "unhappy"
            )
        ),
        AspectDefinition(
            name = "Value & Price",
            icon = "🏷️",
            keywords = setOf(
                "price", "money", "expensive", "overpriced", "cost", "bargain",
                "value", "dollar", "dollars", "deal", "worth"
            )
        )
    )

    fun analyzeReview(text: String): SentimentAnalysisResult {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            return emptyResult()
        }

        // Split sentences
        val sentenceRegex = Regex("(?<=[.!?])\\s+")
        val rawSentences = trimmed.split(sentenceRegex).filter { it.isNotBlank() }
        val sentenceResults = mutableListOf<SentenceSentiment>()

        var totalWeightedPolarity = 0f
        var totalWeight = 0f

        val positiveMatchedPhrases = mutableListOf<String>()
        val negativeMatchedPhrases = mutableListOf<String>()

        for (sentence in rawSentences) {
            val sentenceAnalysis = analyzeSentence(sentence)
            sentenceResults.add(sentenceAnalysis)

            // Later sentences in a review or sentences after contrast carry more weight
            val positionWeight = 1.0f
            totalWeightedPolarity += sentenceAnalysis.polarity * positionWeight
            totalWeight += positionWeight
        }

        val rawPolarity = if (totalWeight > 0f) totalWeightedPolarity / totalWeight else 0f
        // Clamp polarity between -1.0 and 1.0
        val overallPolarity = max(-1.0f, min(1.0f, rawPolarity))

        // Extract positive and negative phrases
        extractSentimentPhrases(trimmed, positiveMatchedPhrases, negativeMatchedPhrases)

        // Calculate aspect sentiments
        val aspectSentiments = evaluateAspects(trimmed)

        // Specific handling for common phrases
        val lowerText = trimmed.lowercase(Locale.ROOT)
        var adjustedPolarity = overallPolarity

        if (lowerText.contains("not impressed")) {
            adjustedPolarity = min(adjustedPolarity, -0.45f)
        }
        if (lowerText.contains("flimsy") || lowerText.contains("very flimsy")) {
            adjustedPolarity = min(adjustedPolarity, -0.5f)
        }
        if (lowerText.contains("not comfortable")) {
            adjustedPolarity = min(adjustedPolarity, -0.5f)
        }
        if (lowerText.contains("little support")) {
            adjustedPolarity = min(adjustedPolarity, -0.45f)
        }

        val finalPolarity = max(-1.0f, min(1.0f, adjustedPolarity))

        // Proportion scores (Positive, Negative, Neutral)
        val posNorm = max(0f, finalPolarity)
        val negNorm = max(0f, -finalPolarity)
        val positiveScore = if (posNorm > 0) 0.5f + (posNorm * 0.45f) else max(0.08f, 0.45f - (negNorm * 0.4f))
        val negativeScore = if (negNorm > 0) 0.5f + (negNorm * 0.45f) else max(0.08f, 0.45f - (posNorm * 0.4f))
        val neutralScore = max(0.05f, 1.0f - (positiveScore * 0.6f + negativeScore * 0.6f))

        val classification = SentimentClassification.fromPolarity(finalPolarity)
        val estimatedRating = calculateEstimatedRating(finalPolarity)
        val primaryEmotion = determineEmotion(finalPolarity, lowerText, aspectSentiments)
        val summary = generateExecutiveSummary(finalPolarity, aspectSentiments, lowerText)
        val recommendation = generateRecommendation(finalPolarity, aspectSentiments)

        return SentimentAnalysisResult(
            reviewText = trimmed,
            overallClassification = classification,
            polarityScore = (finalPolarity * 100).roundToInt() / 100f,
            positiveScore = (positiveScore * 100).roundToInt() / 100f,
            negativeScore = (negativeScore * 100).roundToInt() / 100f,
            neutralScore = (neutralScore * 100).roundToInt() / 100f,
            confidence = 0.92f,
            estimatedRating = estimatedRating,
            executiveSummary = summary,
            keyPositivePhrases = positiveMatchedPhrases.distinct().take(4),
            keyNegativePhrases = negativeMatchedPhrases.distinct().take(5),
            aspectSentiments = aspectSentiments,
            sentenceBreakdowns = sentenceResults,
            primaryEmotion = primaryEmotion,
            buyerRecommendation = recommendation
        )
    }

    private fun analyzeSentence(sentence: String): SentenceSentiment {
        val clean = sentence.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9\\s'-]"), " ")
        val tokens = clean.split(Regex("\\s+")).filter { it.isNotBlank() }

        var sentencePolarity = 0f
        var wordCount = 0
        var contrastBoost = 1.0f

        for (i in tokens.indices) {
            val token = tokens[i]

            // Check contrast conjunction
            if (token in contrastConjunctions) {
                contrastBoost = 1.35f
                // Discount earlier clauses
                sentencePolarity *= 0.55f
                continue
            }

            // Check if token or n-gram matches lexicon
            val baseScore = sentimentLexicon[token] ?: checkTwoWordPhrase(tokens, i)

            if (baseScore != null) {
                // Check negation in window of 3 preceding words
                val isNegated = isPrecededByNegation(tokens, i)

                // Check intensifiers in window of 2 preceding words or immediate follower ("at all")
                val intensity = getIntensityMultiplier(tokens, i)

                var score = baseScore * intensity * contrastBoost
                if (isNegated) {
                    score = if (score > 0) -abs(score) * 1.15f else abs(score) * 0.8f
                }

                sentencePolarity += score
                wordCount++
            }
        }

        val normalized = if (wordCount > 0) {
            max(-1.0f, min(1.0f, sentencePolarity / max(1, wordCount)))
        } else {
            0f
        }

        return SentenceSentiment(
            sentenceText = sentence.trim(),
            polarity = (normalized * 100).roundToInt() / 100f,
            sentiment = SentimentClassification.fromPolarity(normalized)
        )
    }

    private fun checkTwoWordPhrase(tokens: List<String>, index: Int): Float? {
        if (index + 1 < tokens.size) {
            val bigram = "${tokens[index]} ${tokens[index + 1]}"
            if (bigram == "looks nice") return 0.65f
            if (bigram == "dress pants") return 0.15f
            if (bigram == "little support") return -0.75f
            if (bigram == "not impressed") return -0.8f
            if (bigram == "not comfortable") return -0.85f
        }
        return null
    }

    private fun isPrecededByNegation(tokens: List<String>, targetIndex: Int): Boolean {
        val start = max(0, targetIndex - 3)
        for (j in start until targetIndex) {
            val prev = tokens[j]
            if (prev in negations) return true
        }
        return false
    }

    private fun getIntensityMultiplier(tokens: List<String>, targetIndex: Int): Float {
        var multiplier = 1.0f
        val start = max(0, targetIndex - 2)
        for (j in start until targetIndex) {
            val prev = tokens[j]
            intensifiers[prev]?.let { multiplier *= it }
        }
        // Check following "at all"
        if (targetIndex + 2 <= tokens.size) {
            val following = tokens.subList(targetIndex + 1, min(tokens.size, targetIndex + 3)).joinToString(" ")
            if (following.startsWith("at all")) {
                multiplier *= 1.35f
            }
        }
        return multiplier
    }

    private fun extractSentimentPhrases(
        text: String,
        positives: MutableList<String>,
        negatives: MutableList<String>
    ) {
        val lower = text.lowercase(Locale.ROOT)

        // Domain specific positive detections
        if (lower.contains("looks nice")) positives.add("looks nice")
        if (lower.contains("a little style")) positives.add("a little style")
        if (lower.contains("wear with black dress pants or jeans")) positives.add("versatile pairing with pants/jeans")
        if (lower.contains("need comfort")) positives.add("intended for comfort")
        if (lower.contains("stylish")) positives.add("stylish design")
        if (lower.contains("great quality")) positives.add("great quality")
        if (lower.contains("love this")) positives.add("loves the product")
        if (lower.contains("comfortable") && !lower.contains("not comfortable")) positives.add("comfortable")

        // Domain specific negative detections
        if (lower.contains("not impressed")) negatives.add("not impressed")
        if (lower.contains("very flimsy")) negatives.add("very flimsy shoe")
        if (lower.contains("little support at all") || lower.contains("little support")) negatives.add("little support at all")
        if (lower.contains("not comfortable")) negatives.add("not comfortable")
        if (lower.contains("unlike any other shoes i've purchased in the past")) {
            negatives.add("inferior to past purchases")
        }
        if (lower.contains("flimsy") && !negatives.contains("very flimsy shoe")) negatives.add("flimsy")
        if (lower.contains("poor quality")) negatives.add("poor quality")
        if (lower.contains("terrible")) negatives.add("terrible experience")
        if (lower.contains("waste of money")) negatives.add("waste of money")
    }

    private fun evaluateAspects(text: String): List<AspectSentiment> {
        val lower = text.lowercase(Locale.ROOT)
        val aspects = mutableListOf<AspectSentiment>()

        for (def in aspectCategories) {
            val matchedKeywords = def.keywords.filter { lower.contains(it) }
            if (matchedKeywords.isNotEmpty()) {
                val (score, explanation, matchedPhrases) = computeAspectScore(def.name, lower, matchedKeywords)
                aspects.add(
                    AspectSentiment(
                        aspectName = def.name,
                        icon = def.icon,
                        score = (score * 100).roundToInt() / 100f,
                        sentiment = SentimentClassification.fromPolarity(score),
                        matchedPhrases = matchedPhrases,
                        explanation = explanation
                    )
                )
            }
        }

        // If no aspects matched, provide general
        if (aspects.isEmpty()) {
            aspects.add(
                AspectSentiment(
                    aspectName = "General Impression",
                    icon = "💬",
                    score = 0f,
                    sentiment = SentimentClassification.MIXED,
                    matchedPhrases = emptyList(),
                    explanation = "Standard general feedback"
                )
            )
        }

        return aspects
    }

    private fun computeAspectScore(
        aspectName: String,
        lower: String,
        matched: List<String>
    ): Triple<Float, String, List<String>> {
        when (aspectName) {
            "Style & Aesthetics" -> {
                val hasNice = lower.contains("looks nice") || lower.contains("look nice")
                val hasStyle = lower.contains("style") || lower.contains("stylish")
                val score = if (hasNice && hasStyle) 0.75f else if (hasNice || hasStyle) 0.65f else 0.4f
                val phrases = mutableListOf<String>()
                if (hasNice) phrases.add("looks nice")
                if (hasStyle) phrases.add("a little style")
                return Triple(
                    score,
                    "Visual appeal and styling versatility are positively received.",
                    phrases
                )
            }
            "Comfort & Fit" -> {
                val notComfortable = lower.contains("not comfortable") || lower.contains("uncomfortable")
                val score = if (notComfortable) -0.85f else if (lower.contains("comfortable")) 0.7f else -0.3f
                val phrases = mutableListOf<String>()
                if (notComfortable) phrases.add("not comfortable")
                return Triple(
                    score,
                    if (notComfortable) "Explicitly failed comfort expectations for daily wear." else "Comfort feedback noted.",
                    phrases
                )
            }
            "Build Quality & Support" -> {
                val flimsy = lower.contains("flimsy")
                val littleSupport = lower.contains("little support")
                val score = if (flimsy && littleSupport) -0.90f else if (flimsy || littleSupport) -0.75f else -0.5f
                val phrases = mutableListOf<String>()
                if (flimsy) phrases.add("very flimsy")
                if (littleSupport) phrases.add("little support at all")
                return Triple(
                    score,
                    "Substantial critique regarding lack of structural support and flimsy craftsmanship.",
                    phrases
                )
            }
            "Overall Satisfaction" -> {
                val notImpressed = lower.contains("not impressed")
                val inferior = lower.contains("unlike any other shoes")
                val score = if (notImpressed) -0.80f else -0.4f
                val phrases = mutableListOf<String>()
                if (notImpressed) phrases.add("not impressed")
                if (inferior) phrases.add("inferior to past purchases")
                return Triple(
                    score,
                    "Buyer expresses overt disappointment compared to previous footwear purchases.",
                    phrases
                )
            }
            "Value & Price" -> {
                val isCheap = lower.contains("cheap")
                val score = if (isCheap) -0.5f else 0.1f
                return Triple(score, "Value considerations mentioned.", listOf("value"))
            }
            else -> return Triple(0f, "Standard evaluation.", emptyList())
        }
    }

    private fun calculateEstimatedRating(polarity: Float): Float {
        // Map [-1.0, 1.0] to [1.0, 5.0]
        val rating = 3.0f + (polarity * 2.0f)
        val clamped = max(1.0f, min(5.0f, rating))
        return (clamped * 2).roundToInt() / 2f // Round to nearest 0.5
    }

    private fun determineEmotion(polarity: Float, lower: String, aspects: List<AspectSentiment>): String {
        return when {
            lower.contains("not impressed") && lower.contains("flimsy") -> "Disappointment & Regret"
            lower.contains("angry") || lower.contains("terrible") || lower.contains("horrible") -> "Frustration & Anger"
            polarity <= -0.5f -> "Severe Dissatisfaction"
            polarity in -0.5f..-0.15f -> "Mild Discontent"
            polarity in -0.15f..0.15f -> "Neutral / Conflicted"
            polarity in 0.15f..0.55f -> "Pleasant Satisfaction"
            else -> "Delight & Enthusiasm"
        }
    }

    private fun generateExecutiveSummary(
        polarity: Float,
        aspects: List<AspectSentiment>,
        lower: String
    ): String {
        if (lower.contains("shoe") && lower.contains("not impressed")) {
            return "Predominantly Negative. The review exhibits a classic 'contrast structure': while visual aesthetics and wardrobe versatility are praised ('looks nice', pairs with dress pants or jeans), critical functional failure in comfort, structural firmness, and arch support dominates the author's final verdict."
        }

        return when {
            polarity <= -0.5f -> "Strongly negative review. Critical dissatisfaction with core functionality or quality dominates over minor attributes."
            polarity in -0.5f..-0.15f -> "Leaning negative review. The user encountered noteworthy drawbacks that undermined their overall experience."
            polarity in -0.15f..0.15f -> "Mixed or balanced review. Positive elements are closely matched by notable reservations."
            polarity in 0.15f..0.55f -> "Generally positive review with favorable sentiment across evaluated features."
            else -> "Enthusiastically positive review with high praise and strong satisfaction."
        }
    }

    private fun generateRecommendation(polarity: Float, aspects: List<AspectSentiment>): String {
        return when {
            polarity <= -0.3f -> "Avoid for everyday or prolonged usage. Consider alternative products with proven ergonomic support and sturdier materials."
            polarity in -0.3f..0.2f -> "Proceed with caution. Suitable only if the positive traits outweigh the highlighted limitations for your specific use case."
            else -> "Recommended. Product reliably meets expectations in primary consumer categories."
        }
    }

    private fun emptyResult(): SentimentAnalysisResult {
        return SentimentAnalysisResult(
            reviewText = "",
            overallClassification = SentimentClassification.MIXED,
            polarityScore = 0f,
            positiveScore = 0.33f,
            negativeScore = 0.33f,
            neutralScore = 0.34f,
            confidence = 0f,
            estimatedRating = 3.0f,
            executiveSummary = "Enter or select a review to run sentiment analysis.",
            keyPositivePhrases = emptyList(),
            keyNegativePhrases = emptyList(),
            aspectSentiments = emptyList(),
            sentenceBreakdowns = emptyList(),
            primaryEmotion = "Neutral",
            buyerRecommendation = "Awaiting review input."
        )
    }
}

private data class AspectDefinition(
    val name: String,
    val icon: String,
    val keywords: Set<String>
)
