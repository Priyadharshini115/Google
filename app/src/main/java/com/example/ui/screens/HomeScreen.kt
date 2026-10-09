package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.SampleReviewsData
import com.example.domain.SentimentAnalyzerEngine
import com.example.ui.SentimentViewModel
import com.example.ui.components.AspectSentimentCard
import com.example.ui.components.ReviewInteractiveHighlighter
import com.example.ui.components.SentimentBadge
import com.example.ui.components.SentimentDistributionBar
import com.example.ui.components.SentimentGauge
import com.example.ui.components.StarRatingDisplay
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentPositive

@Composable
fun HomeScreen(
    viewModel: SentimentViewModel,
    modifier: Modifier = Modifier
) {
    val featuredSample = SampleReviewsData.USER_FEATURED_REVIEW
    // Compute or fetch result for the featured shoe review
    val featuredResult = SentimentAnalyzerEngine.analyzeReview(featuredSample.reviewText)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_content")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header Answer Banner
            HeroInquiryAnswerCard(
                result = featuredResult,
                onInspectInLab = {
                    viewModel.loadSample(featuredSample)
                    viewModel.setActiveTab(1) // Navigate to Analyzer
                }
            )
        }

        item {
            Text(
                text = "Highlighted Review Text",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            ReviewInteractiveHighlighter(
                text = featuredSample.reviewText,
                positivePhrases = featuredResult.keyPositivePhrases,
                negativePhrases = featuredResult.keyNegativePhrases
            )
        }

        item {
            // Sentiment Polarity & Distribution Metrics
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Sentiment Metrics & Polarity",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SentimentGauge(
                            polarity = featuredResult.polarityScore,
                            classification = featuredResult.overallClassification,
                            size = 130.dp
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            SentimentBadge(classification = featuredResult.overallClassification)
                            StarRatingDisplay(rating = featuredResult.estimatedRating)
                            Text(
                                text = "Dominant Emotion: ${featuredResult.primaryEmotion}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    SentimentDistributionBar(
                        positive = featuredResult.positiveScore,
                        neutral = featuredResult.neutralScore,
                        negative = featuredResult.negativeScore
                    )
                }
            }
        }

        item {
            // Pros vs Cons Drivers Card
            ProsConsCard(
                positives = featuredResult.keyPositivePhrases,
                negatives = featuredResult.keyNegativePhrases
            )
        }

        item {
            Text(
                text = "Aspect-Based Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Granular inspection of style vs. ergonomics vs. construction",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(featuredResult.aspectSentiments) { aspect ->
            AspectSentimentCard(aspect = aspect)
        }

        item {
            // Sentence timeline
            SentenceProgressionCard(sentences = featuredResult.sentenceBreakdowns)
        }

        item {
            // Recommendation Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Key Takeaway & Verdict",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = featuredResult.buyerRecommendation,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun HeroInquiryAnswerCard(
    result: com.example.data.model.SentimentAnalysisResult,
    onInspectInLab: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            SentimentNegative.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
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
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "QUESTION RESOLUTION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp,
                        color = IndigoPrimary
                    )
                }

                SentimentBadge(classification = result.overallClassification)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Sentiment: Negative",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = SentimentNegative
            )

            Text(
                text = "Predominantly dissatisfied customer review",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = result.executiveSummary,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onInspectInLab,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inspect_in_lab_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open in Sentiment Lab",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ProsConsCard(
    positives: List<String>,
    negatives: List<String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Key Contrast Drivers",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Positives Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SentimentPositive.copy(alpha = 0.08f))
                        .border(1.dp, SentimentPositive.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SentimentPositive,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Positives",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SentimentPositive
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    positives.forEach { item ->
                        Text(
                            text = "• $item",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }

                // Negatives Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SentimentNegative.copy(alpha = 0.08f))
                        .border(1.dp, SentimentNegative.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = SentimentNegative,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Criticisms",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SentimentNegative
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    negatives.forEach { item ->
                        Text(
                            text = "• $item",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SentenceProgressionCard(
    sentences: List<com.example.data.model.SentenceSentiment>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Sentence Polarity Flow",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Tracking how sentiment shifts across each thought",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))

            sentences.forEachIndexed { index, sentence ->
                val color = when (sentence.sentiment) {
                    com.example.data.model.SentimentClassification.POSITIVE,
                    com.example.data.model.SentimentClassification.STRONGLY_POSITIVE -> SentimentPositive
                    com.example.data.model.SentimentClassification.NEGATIVE,
                    com.example.data.model.SentimentClassification.STRONGLY_NEGATIVE -> SentimentNegative
                    com.example.data.model.SentimentClassification.MIXED -> Color.Gray
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = color
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "“${sentence.sentenceText}”",
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${sentence.polarity}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
            }
        }
    }
}
