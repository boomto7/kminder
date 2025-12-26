package com.kminder.minder.ui.component

import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.offset
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
    containerColor: Color = MaterialTheme.colorScheme.primary, // Default to Primary
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    borderColor: Color = Color.Black, // Bold Black Border
    shadowColor: Color = Color.Black, // Hard Shadow Color
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // 클릭 시 눌림 효과 (Scale + Shadow Offset 감소)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "button_scale"
    )
    
    val shadowOffset by animateDpAsState(
        targetValue = if (isPressed) 0.dp else 4.dp, // 눌리면 그림자가 없어짐 (바닥에 붙음)
        label = "shadow_offset"
    )

    Box(
        modifier = modifier
            .scale(scale)
            // 그림자가 짤리지 않도록 충분한 공간 확보 필요할 수 있음 (외부 padding 권장)
            ,
        contentAlignment = Alignment.Center
    ) {
        if (enabled) {
            // Shadow Layer (Hard Shadow)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, shape)
            )
        }

        // Main Surface Layer
        androidx.compose.material3.Surface(
            onClick = onClick,
            modifier = Modifier,
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

