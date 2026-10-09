package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentPositive
import java.util.Locale

@Composable
fun ReviewInteractiveHighlighter(
    text: String,
    positivePhrases: List<String>,
    negativePhrases: List<String>,
    modifier: Modifier = Modifier
) {
    val annotatedText = buildAnnotatedString {
        val lowerText = text.lowercase(Locale.ROOT)

        // Find match ranges
        data class PhraseSpan(val start: Int, val end: Int, val isPositive: Boolean)
        val spans = mutableListOf<PhraseSpan>()

        positivePhrases.forEach { phrase ->
            val pLower = phrase.lowercase(Locale.ROOT)
            var index = lowerText.indexOf(pLower)
            while (index >= 0) {
                spans.add(PhraseSpan(index, index + pLower.length, true))
                index = lowerText.indexOf(pLower, index + pLower.length)
            }
        }

        negativePhrases.forEach { phrase ->
            val pLower = phrase.lowercase(Locale.ROOT)
            var index = lowerText.indexOf(pLower)
            while (index >= 0) {
                spans.add(PhraseSpan(index, index + pLower.length, false))
                index = lowerText.indexOf(pLower, index + pLower.length)
            }
        }

        // Sort by start index and eliminate overlaps
        val sortedSpans = spans.sortedBy { it.start }
        val nonOverlapping = mutableListOf<PhraseSpan>()
        var lastEnd = 0
        for (span in sortedSpans) {
            if (span.start >= lastEnd) {
                nonOverlapping.add(span)
                lastEnd = span.end
            }
        }

        var currentIndex = 0
        for (span in nonOverlapping) {
            if (span.start > currentIndex) {
                append(text.substring(currentIndex, span.start))
            }

            val color = if (span.isPositive) SentimentPositive else SentimentNegative
            val bg = color.copy(alpha = 0.2f)

            withStyle(
                style = SpanStyle(
                    color = color,
                    background = bg,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(text.substring(span.start, span.end))
            }
            currentIndex = span.end
        }

        if (currentIndex < text.length) {
            append(text.substring(currentIndex))
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SentimentPositive)
                    )
                    Text(
                        text = "Positive phrasing",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(SentimentNegative)
                    )
                    Text(
                        text = "Negative phrasing",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
