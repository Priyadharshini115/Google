package com.example

import com.example.data.model.SentimentClassification
import com.example.domain.SentimentAnalyzerEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SentimentAnalyzerEngineTest {

    @Test
    fun testUserShoeReviewSentimentIsNegative() {
        val review = "This is a shoe I will wear with black dress pants or jeans when I need comfort and a little style, but I am not impressed. This is a very flimsy shoe with little support at all. Unlike any other shoes I've purchased in the past. It looks nice, but it's not comfortable."

        val result = SentimentAnalyzerEngine.analyzeReview(review)

        // Must classify as Negative
        assertTrue(
            "Overall sentiment should be Negative or Strongly Negative, but was ${result.overallClassification}",
            result.overallClassification == SentimentClassification.NEGATIVE ||
                    result.overallClassification == SentimentClassification.STRONGLY_NEGATIVE
        )

        // Polarity score must be negative
        assertTrue("Polarity should be negative (< 0)", result.polarityScore < 0)

        // Estimated rating should reflect a dissatisfied customer (<= 2.5 stars)
        assertTrue("Estimated rating should be <= 2.5", result.estimatedRating <= 2.5f)

        // Check key phrases detected
        assertTrue("Should detect negative phrase 'not impressed'",
            result.keyNegativePhrases.any { it.contains("not impressed") })
        assertTrue("Should detect negative phrase 'little support'",
            result.keyNegativePhrases.any { it.contains("little support") })
        assertTrue("Should detect positive phrase 'looks nice'",
            result.keyPositivePhrases.any { it.contains("looks nice") })

        // Check aspects
        val styleAspect = result.aspectSentiments.find { it.aspectName.contains("Style") }
        val comfortAspect = result.aspectSentiments.find { it.aspectName.contains("Comfort") }
        val supportAspect = result.aspectSentiments.find { it.aspectName.contains("Support") || it.aspectName.contains("Quality") }

        assertTrue("Style aspect should be positive", (styleAspect?.score ?: 0f) > 0f)
        assertTrue("Comfort aspect should be negative", (comfortAspect?.score ?: 0f) < 0f)
        assertTrue("Support aspect should be negative", (supportAspect?.score ?: 0f) < 0f)
    }

    @Test
    fun testPositiveReviewClassifiesPositive() {
        val positiveReview = "The active noise cancellation on these headphones is truly phenomenal! The soundstage is rich and immersive, and the ear cushions are super comfortable."
        val result = SentimentAnalyzerEngine.analyzeReview(positiveReview)
        assertTrue(
            "Should classify as Positive, was ${result.overallClassification}",
            result.overallClassification == SentimentClassification.POSITIVE ||
                    result.overallClassification == SentimentClassification.STRONGLY_POSITIVE
        )
    }
}
