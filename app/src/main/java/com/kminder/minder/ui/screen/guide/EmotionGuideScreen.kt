package com.kminder.minder.ui.screen.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kminder.domain.model.EmotionType
import com.kminder.minder.R
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.util.EmotionColorUtil

@Composable
fun EmotionGuideScreen(
    onNavigateBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinderBackground)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            GuideHeader(onNavigateBack)

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Plutchik's Wheel Intro
                GuideSectionCard(
                    title = stringResource(R.string.guide_plutchik_title),
                    content = stringResource(R.string.guide_plutchik_desc)
                )

                // 2. Basic Emotions (w/ Color Chips)
                GuideSectionCard(
                    title = stringResource(R.string.guide_basic_emotions_title),
                    content = stringResource(R.string.guide_basic_emotions_desc)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    EmotionColorGrid()
                }

                // 3. Complex Emotions
                GuideSectionCard(
                    title = stringResource(R.string.guide_complex_emotions_title),
                    content = stringResource(R.string.guide_complex_emotions_desc)
                )

                // 4. Intensity
                GuideSectionCard(
                    title = stringResource(R.string.guide_intensity_title),
                    content = stringResource(R.string.guide_intensity_desc)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun GuideHeader(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RetroIconButton(
            onClick = onNavigateBack,
            icon = Icons.Default.ArrowBack,
            contentDescription = stringResource(R.string.common_back)
        )
        Text(
            text = stringResource(R.string.guide_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
        // Balance spacer
        Spacer(modifier = Modifier.size(50.dp))
    }
}

@Composable
fun GuideSectionCard(
    title: String,
    content: String,
    contentBuilder: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = 4.dp, y = 4.dp)
                .background(Color.Black, RoundedCornerShape(16.dp))
        )
        // Main
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, RoundedCornerShape(16.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                contentBuilder()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmotionColorGrid() {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        EmotionType.entries.forEach { emotion ->
            EmotionChip(emotion)
        }
    }
}

@Composable
fun EmotionChip(emotion: EmotionType) {
    val color = EmotionColorUtil.getEmotionColor(emotion)
    val nameResId = EmotionColorUtil.getEmotionNameResId(emotion)
    Surface(
        shape = CircleShape,
        color = color,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
    ) {
        Text(
            text = stringResource(nameResId),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (emotion == EmotionType.ANGER || emotion == EmotionType.SADNESS) Color.White else Color.Black, // Simple contrast logic
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
