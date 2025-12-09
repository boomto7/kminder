package com.kminder.minder.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kminder.minder.ui.component.MinderButton
import com.kminder.minder.ui.component.MinderGlassCard

@Composable
fun ColorPaletteSection(title: String, colors: List<Pair<String, Color>>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            colors.forEach { (name, color) ->
                ColorItem(name = name, color = color)
            }
        }
    }
}

@Composable
fun ColorItem(name: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(color = color, shape = RoundedCornerShape(12.dp))
                .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 1000)
@Composable
fun ColorTemplatePreview() {
    MinderTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Minder Design System",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // 1. Plutchik Emotions
            ColorPaletteSection(
                title = "Plutchik Emotions",
                colors = listOf(
                    "Joy" to EmotionJoy,
                    "Trust" to EmotionTrust,
                    "Fear" to EmotionFear,
                    "Surprise" to EmotionSurprise,
                    "Sadness" to EmotionSadness,
                    "Disgust" to EmotionDisgust,
                    "Anger" to EmotionAnger,
                    "Anticipation" to EmotionAnticipation
                )
            )

            // 2. Main Theme Colors
            ColorPaletteSection(
                title = "Theme Colors (Pastel)",
                colors = listOf(
                    "Primary" to MinderLavender,
                    "Secondary" to MinderSkyBlue,
                    "Tertiary" to MinderPeach,
                    "Mint" to MinderMint,
                    "Background" to MinderBackground,
                    "Surface" to MinderSurface
                )
            )

            // 3. Components
            Text(
                text = "Components",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Glass Card (Now Gradient Card)
            MinderGlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Gradient Card Component",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This is a gradient card with soft pastel colors and shadow for depth.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MinderButton(onClick = {}) {
                    Text("Glass Button")
                }
                
                MinderButton(
                    onClick = {},
                    containerColor = MinderSkyBlue.copy(alpha = 0.5f),
                    contentColor = MinderTextDark
                ) {
                    Text("Secondary")
                }
            }
            
            // Usage Example with Emotion Color
            MinderGlassCard(
                modifier = Modifier.fillMaxWidth(),
                gradient = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(EmotionJoy, Color.White)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(EmotionJoy, RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Emotion Card (Joy Gradient)")
                }
            }
        }
    }
}
