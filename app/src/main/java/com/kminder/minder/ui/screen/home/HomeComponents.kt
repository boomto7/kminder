package com.kminder.minder.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.theme.MinderBackground

@Composable
fun OutlinedDivider(
    modifier: Modifier = Modifier,
    color: Color,
    strokeWidth: Dp = 1.dp,
    cornerRadius: Dp = 8.dp
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val strokeWidthPx = strokeWidth.toPx()
        drawRoundRect(
            color = color,
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            size = Size(size.width - strokeWidthPx, size.height - strokeWidthPx),
            cornerRadius = CornerRadius(x = 0f, y = 0f), // Sharp corners or small radius
            style = Stroke(width = strokeWidthPx)
        )
    }
}

@Composable
fun OutlinedTimeText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    strokeWidth: Float = 8f,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        // 1. 외곽선 (Stroke)
        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold, // Boldest font
                fontSize = fontSize,
                drawStyle = Stroke(
                    width = strokeWidth,
                    join = StrokeJoin.Miter // Sharp joints
                )
            ),
            color = Color.Black // Always black border for Retro style
        )
        // 2. 내부 채우기 (Fill)
        Text(
            text = text,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.ExtraBold, // Boldest font
                fontSize = fontSize
            ),
            color = MinderBackground // Fill Color
        )
    }
}

@Composable
fun RetroFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String
) {
    NeoShadowBox(
        modifier = Modifier.size(64.dp).clickable(onClick = onClick),
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun FeedGroupingSelector(
    selectedOption: FeedGroupingOption,
    onOptionSelected: (FeedGroupingOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FeedGroupingOption.entries.forEach { option ->
            val isSelected = option == selectedOption
            val label = when (option) {
                FeedGroupingOption.DAILY -> "일간"
                FeedGroupingOption.WEEKLY -> "주간"
                FeedGroupingOption.MONTHLY -> "월간"
            }

                NeoShadowBox(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onOptionSelected(option) },
                containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                shape = RoundedCornerShape(8.dp),
                offset = if (isSelected) 2.dp else 4.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun previewFeedGroupingSelector() {
    FeedGroupingSelector(selectedOption = FeedGroupingOption.DAILY, onOptionSelected = {})
}