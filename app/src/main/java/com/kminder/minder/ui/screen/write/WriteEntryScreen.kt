package com.kminder.minder.ui.screen.write

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 일기 작성 화면 (Placeholder)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteEntryScreen(
    onNavigateBack: () -> Unit,
    onEntrySaved: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("일기 작성") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text("일기 작성 화면 (구현 예정)")
        }
    }
}
