package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AspectSentiment
import com.example.data.model.SentimentClassification
import com.example.ui.theme.SentimentMixed
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentPositive

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AspectSentimentCard(
    aspect: AspectSentiment,
    modifier: Modifier = Modifier
) {
    val barColor = when (aspect.sentiment) {
        SentimentClassification.POSITIVE,
        SentimentClassification.STRONGLY_POSITIVE -> SentimentPositive
        SentimentClassification.NEGATIVE,
        SentimentClassification.STRONGLY_NEGATIVE -> SentimentNegative
        SentimentClassification.MIXED -> SentimentMixed
    }

    val normalizedScore = ((aspect.score + 1f) / 2f).coerceIn(0f, 1f)
    val scorePercentage = (normalizedScore * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = aspect.icon, fontSize = 20.sp)
                    Text(
                        text = aspect.aspectName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Compact sentiment indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(barColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = aspect.sentiment.label,
                        color = barColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(normalizedScore)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "${if (aspect.score > 0) "+" else ""}${aspect.score}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = barColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = aspect.explanation,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            if (aspect.matchedPhrases.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    aspect.matchedPhrases.forEach { phrase ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .border(
                                    width = 1.dp,
                                    color = barColor.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .background(barColor.copy(alpha = 0.08f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "“$phrase”",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = barColor
                            )
                        }
                    }
                }
            }
        }
    }
}
