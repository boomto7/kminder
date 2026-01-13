package com.kminder.minder.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * 화면 전체를 덮는 로딩 화면 (터치 차단)
 *
 * @param message 표시할 메시지 (null이면 텍스트 없이 로딩바만 표시)
 * @param isVisible 보여줄지 여부
 */
@Composable
fun BlockingLoadingOverlay(
    isVisible: Boolean,
    message: String? = null,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(99f) // 최상위 배치
            .background(Color.Black.copy(alpha = 0.2f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // 터치 효과 없음
                onClick = { /* 터치 차단 */ }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RetroLoadingIndicator(
                modifier = Modifier.size(48.dp),
            )

            if (message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun BlockingLoadingOverlayPreview() {
    MaterialTheme {
        BlockingLoadingOverlay(
            isVisible = true,
            message = "분석 중..."
        )
    }
}

@Preview
@Composable
fun BlockingLoadingOverlayNoTextPreview() {
    MaterialTheme {
        BlockingLoadingOverlay(
            isVisible = true,
            message = null
        )
    }
}
