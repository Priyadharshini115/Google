package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SentimentClassification(val label: String, val emoji: String) {
    POSITIVE("Positive", "😊"),
    NEGATIVE("Negative", "😞"),
    MIXED("Mixed / Neutral", "⚖️"),
    STRONGLY_NEGATIVE("Strongly Negative", "😡"),
    STRONGLY_POSITIVE("Strongly Positive", "🤩");

    companion object {
        fun fromPolarity(polarity: Float): SentimentClassification {
            return when {
                polarity >= 0.5f -> STRONGLY_POSITIVE
                polarity in 0.15f..0.5f -> POSITIVE
                polarity in -0.15f..0.15f -> MIXED
                polarity in -0.5f..-0.15f -> NEGATIVE
                else -> STRONGLY_NEGATIVE
            }
        }
    }
}

data class AspectSentiment(
    val aspectName: String,
    val icon: String,
    val score: Float, // -1.0 to 1.0
    val sentiment: SentimentClassification,
    val matchedPhrases: List<String>,
    val explanation: String
)

data class SentenceSentiment(
    val sentenceText: String,
    val polarity: Float,
    val sentiment: SentimentClassification
)

data class SentimentAnalysisResult(
    val id: String = "",
    val reviewText: String,
    val overallClassification: SentimentClassification,
    val polarityScore: Float, // -1.0 to 1.0
    val positiveScore: Float, // 0.0 to 1.0
    val negativeScore: Float, // 0.0 to 1.0
    val neutralScore: Float,  // 0.0 to 1.0
    val confidence: Float,    // 0.0 to 1.0
    val estimatedRating: Float, // 1.0 to 5.0 stars
    val executiveSummary: String,
    val keyPositivePhrases: List<String>,
    val keyNegativePhrases: List<String>,
    val aspectSentiments: List<AspectSentiment>,
    val sentenceBreakdowns: List<SentenceSentiment>,
    val primaryEmotion: String,
    val buyerRecommendation: String,
    val analyzedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_reviews")
data class SavedReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val reviewText: String,
    val category: String,
    val classification: String,
    val polarityScore: Float,
    val estimatedRating: Float,
    val executiveSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)
