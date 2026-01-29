package com.kminder.minder.ui.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kminder.minder.ui.theme.MinderLavender
import com.kminder.minder.ui.theme.MinderSkyBlue

/**
 * Minder 앱 전용 프리미엄 버튼
 * - 그라디언트 배경 지원
 * - 부드러운 그림자 (Glow 효과)
 * - 클릭 시 스케일 애니메이션
 */
@Composable
fun MinderButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(16.dp),
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    containerColor: Color = MaterialTheme.colorScheme.primary, // Default to Primary
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    borderColor: Color = Color.Black, // Bold Black Border
    shadowColor: Color = Color.Black, // Hard Shadow Color
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Physical Press Effect: Move the Surface DOWN to meet the Shadow
    val pressOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        label = "press_offset"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true
    ) {
        if (enabled) {
            // Shadow Layer (Static at 'Ground' Level)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 4.dp, y = 4.dp) // Fixed Shadow Position
                    .background(shadowColor, shape)
            )
        }

        // Main Surface Layer (Moves on Press)
        androidx.compose.material3.Surface(
            onClick = onClick,
            modifier = Modifier
                .offset(x = pressOffset, y = pressOffset), // Animate Position
            enabled = enabled,
            shape = shape,
            color = if (enabled) containerColor else Color.Gray.copy(alpha = 0.2f),
            contentColor = contentColor,
            border = androidx.compose.foundation.BorderStroke(2.dp, if (enabled) borderColor else Color.Gray),
            interactionSource = interactionSource,
            shadowElevation = 0.dp // Disable default soft shadow
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = ButtonDefaults.MinWidth,
                        minHeight = ButtonDefaults.MinHeight
                    )
                    .padding(contentPadding),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProvideTextStyle(value = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)) {
                    content()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMinderButton() {
    MinderButton(
        onClick = {},
        modifier = Modifier.widthIn(min = 160.dp)
    ) {
        Text(text = "기록하러 가기")
    }
}