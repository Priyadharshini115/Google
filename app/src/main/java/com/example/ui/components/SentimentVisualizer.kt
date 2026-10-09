package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SentimentClassification
import com.example.ui.theme.SentimentMixed
import com.example.ui.theme.SentimentNegative
import com.example.ui.theme.SentimentPositive
import kotlin.math.roundToInt

@Composable
fun SentimentBadge(
    classification: SentimentClassification,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (classification) {
        SentimentClassification.POSITIVE,
        SentimentClassification.STRONGLY_POSITIVE -> Triple(
            SentimentPositive.copy(alpha = 0.18f),
            SentimentPositive,
            SentimentPositive.copy(alpha = 0.5f)
        )
        SentimentClassification.NEGATIVE,
        SentimentClassification.STRONGLY_NEGATIVE -> Triple(
            SentimentNegative.copy(alpha = 0.18f),
            SentimentNegative,
            SentimentNegative.copy(alpha = 0.5f)
        )
        SentimentClassification.MIXED -> Triple(
            SentimentMixed.copy(alpha = 0.18f),
            SentimentMixed,
            SentimentMixed.copy(alpha = 0.5f)
        )
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .border(1.dp, borderColor, RoundedCornerShape(100.dp)),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = classification.emoji, fontSize = 16.sp)
            Text(
                text = classification.label.uppercase(),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun SentimentGauge(
    polarity: Float, // -1.0 to 1.0
    classification: SentimentClassification,
    size: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    // Map -1.0..1.0 to 0.0..1.0 for arc sweep
    val normalizedProgress = ((polarity + 1f) / 2f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = normalizedProgress,
        animationSpec = tween(durationMillis = 800),
        label = "gauge_progress"
    )

    val gaugeColor = when (classification) {
        SentimentClassification.POSITIVE,
        SentimentClassification.STRONGLY_POSITIVE -> SentimentPositive
        SentimentClassification.NEGATIVE,
        SentimentClassification.STRONGLY_NEGATIVE -> SentimentNegative
        SentimentClassification.MIXED -> SentimentMixed
    }

    val polarityText = if (polarity > 0) "+${polarity}" else "$polarity"

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size - 12.dp)) {
            val strokeWidth = 14.dp.toPx()
            // Track background arc (240 degrees from 150 to 390)
            drawArc(
                color = Color.Gray.copy(alpha = 0.2f),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Dynamic progress arc
            drawArc(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        SentimentNegative,
                        SentimentMixed,
                        SentimentPositive
                    )
                ),
                startAngle = 150f,
                sweepAngle = 240f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = polarityText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = gaugeColor
            )
            Text(
                text = "Polarity Score",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SentimentDistributionBar(
    positive: Float,
    neutral: Float,
    negative: Float,
    modifier: Modifier = Modifier
) {
    val total = (positive + neutral + negative).coerceAtLeast(0.01f)
    val posPct = ((positive / total) * 100).roundToInt()
    val neuPct = ((neutral / total) * 100).roundToInt()
    val negPct = (100 - posPct - neuPct).coerceAtLeast(0)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Positive: $posPct%",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SentimentPositive
            )
            Text(
                text = "Neutral: $neuPct%",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SentimentMixed
            )
            Text(
                text = "Negative: $negPct%",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SentimentNegative
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            if (posPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(posPct.toFloat().coerceAtLeast(1f))
                        .height(10.dp)
                        .background(SentimentPositive)
                )
            }
            if (neuPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(neuPct.toFloat().coerceAtLeast(1f))
                        .height(10.dp)
                        .background(SentimentMixed)
                )
            }
            if (negPct > 0) {
                Box(
                    modifier = Modifier
                        .weight(negPct.toFloat().coerceAtLeast(1f))
                        .height(10.dp)
                        .background(SentimentNegative)
                )
            }
        }
    }
}

@Composable
fun StarRatingDisplay(
    rating: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..5) {
            val starValue = i.toFloat()
            val icon = when {
                rating >= starValue -> Icons.Filled.Star
                rating >= starValue - 0.5f -> Icons.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            val tint = if (rating >= starValue - 0.5f) Color(0xFFFBBF24) else Color.Gray.copy(alpha = 0.4f)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.1f / 5.0", rating),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
