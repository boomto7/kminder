package com.kminder.minder.ui.screen.write

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kminder.minder.R

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
                title = { Text(stringResource(R.string.write_entry_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
            Text(androidx.compose.ui.res.stringResource(com.kminder.minder.R.string.write_entry_placeholder))
        }
    }
}
