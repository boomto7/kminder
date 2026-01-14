package com.kminder.minder.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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
