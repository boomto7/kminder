package com.kminder.minder.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RetroLoadingIndicator(
    modifier: Modifier = Modifier.size(48.dp)
) {
    NeoShadowBox(
        modifier = modifier,
        shape = CircleShape,
        offset = 3.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(0.6f),
                color = Color.Black,
                strokeWidth = 3.dp
            )
        }
    }
}
