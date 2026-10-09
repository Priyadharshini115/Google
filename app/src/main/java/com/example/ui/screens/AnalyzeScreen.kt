package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SentimentViewModel
import com.example.ui.components.AspectSentimentCard
import com.example.ui.components.ReviewInteractiveHighlighter
import com.example.ui.components.SentimentBadge
import com.example.ui.components.SentimentDistributionBar
import com.example.ui.components.SentimentGauge
import com.example.ui.components.StarRatingDisplay

@Composable
fun AnalyzeScreen(
    viewModel: SentimentViewModel,
    modifier: Modifier = Modifier
) {
    val inputText by viewModel.inputText.collectAsState()
    val currentResult by viewModel.currentResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analyze_screen_content")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sentiment Analysis Lab",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Input custom customer feedback to inspect emotional polarity and aspect metrics",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        Text(
                            text = "Customer Review Text",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row {
                            IconButton(
                                onClick = {
                                    val clip = clipboardManager.getText()?.text
                                    if (!clip.isNullOrBlank()) {
                                        viewModel.setInputText(clip)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste from clipboard",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.setInputText("") }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear input",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.setInputText(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("review_input_field"),
                        placeholder = {
                            Text("Type or paste any product or service review here...")
                        },
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.analyzeInput() },
                        enabled = inputText.isNotBlank() && !isAnalyzing,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("run_analysis_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing Sentiment...")
                        } else {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Analyze Sentiment",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        if (currentResult.reviewText.isNotBlank()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Analysis Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            val copySummary = """
                                Sentiment: ${currentResult.overallClassification.label}
                                Polarity: ${currentResult.polarityScore}
                                Star Rating: ${currentResult.estimatedRating} / 5.0
                                Summary: ${currentResult.executiveSummary}
                            """.trimIndent()
                            clipboardManager.setText(AnnotatedString(copySummary))
                            Toast.makeText(context, "Copied analysis summary!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy analysis",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            item {
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
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SentimentGauge(
                                polarity = currentResult.polarityScore,
                                classification = currentResult.overallClassification,
                                size = 120.dp
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SentimentBadge(classification = currentResult.overallClassification)
                                StarRatingDisplay(rating = currentResult.estimatedRating)
                                Text(
                                    text = "Emotion: ${currentResult.primaryEmotion}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        SentimentDistributionBar(
                            positive = currentResult.positiveScore,
                            neutral = currentResult.neutralScore,
                            negative = currentResult.negativeScore
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentResult.executiveSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Linguistic Span Highlights",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                ReviewInteractiveHighlighter(
                    text = currentResult.reviewText,
                    positivePhrases = currentResult.keyPositivePhrases,
                    negativePhrases = currentResult.keyNegativePhrases
                )
            }

            if (currentResult.aspectSentiments.isNotEmpty()) {
                item {
                    Text(
                        text = "Aspect Sentiments",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(currentResult.aspectSentiments) { aspect ->
                    AspectSentimentCard(aspect = aspect)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
