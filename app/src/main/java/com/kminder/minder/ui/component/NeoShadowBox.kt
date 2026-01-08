package com.kminder.minder.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Neo-Brutalism Style Shadow Box Component
 *
 * It consists of two layers:
 * 1. Shadow Layer (Background Box with offset)
 * 2. Content Layer (Foreground Box with border)
 */
@Composable
fun NeoShadowBox(
    modifier: Modifier = Modifier,
    offset: Dp = 4.dp,
    shadowColor: Color = Color.Black,
    containerColor: Color = Color.White,
    borderColor: Color = Color.Black,
    borderWidth: Dp = 2.dp,
    shape: Shape = RoundedCornerShape(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        // Shadow Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = offset, y = offset)
                .background(shadowColor, shape)
        )

        // Content Layer
        Box(
            modifier = Modifier
                .background(containerColor, shape)
                .border(borderWidth, borderColor, shape)
                .clip(shape),
            content = content
        )
    }
}
