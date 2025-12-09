package com.kminder.minder.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Minder 앱의 그라디언트 카드 (구 GlassCard)
 * - 부드러운 파스텔 톤 그라디언트 배경 적용
 * - 그림자를 통해 깊이감 표현
 */
@Composable
fun MinderGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    gradient: Brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFF59D).copy(alpha = 0.8f), // Soft Yellow
            Color(0xFFC5E1A5).copy(alpha = 0.8f)  // Soft Green
        )
    ),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = shape,
                spotColor = Color.Gray.copy(alpha = 0.2f),
                ambientColor = Color.Gray.copy(alpha = 0.1f)
            )
            .clip(shape)
            .background(brush = gradient)
            .padding(16.dp),
        content = content
    )
}
