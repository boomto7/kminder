package com.kminder.minder.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
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
    containerColor: Color = Color.White.copy(alpha = 0.4f), // Glass Effect
    contentColor: Color = MaterialTheme.colorScheme.primary, // Text Color
    borderColor: Color = Color.White.copy(alpha = 0.6f),
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // 클릭 시 살짝 줄어드는 애니메이션
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "button_scale"
    )

    // 테두리 설정
    val borderStroke = androidx.compose.foundation.BorderStroke(1.dp, borderColor)

    val containerModifier = if (enabled) {
        modifier
            .scale(scale)
            .clip(shape)
            .background(containerColor)
            .border(borderStroke, shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                role = Role.Button
            )
    } else {
        modifier
            .clip(shape)
            .background(Color.Gray.copy(alpha = 0.2f))
    }

    Box(
        modifier = containerModifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight
                )
                .padding(contentPadding),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = contentColor)) {
                content()
            }
        }
    }
}

