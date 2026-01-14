package com.kminder.minder.ui.screen.write

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kminder.minder.R
import com.kminder.minder.ui.component.BlockingLoadingOverlay
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.component.RetroLoadingIndicator
import com.kminder.minder.ui.component.OutlinedDivider
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.ui.theme.MinderTheme

@Composable
fun WriteEntryScreen(
    onNavigateBack: () -> Unit,
    onEntrySaved: (Long) -> Unit,
    viewModel: WriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle Success Navigation
    LaunchedEffect(uiState.isSuccess, uiState.savedEntryId) {
        if (uiState.isSuccess && uiState.savedEntryId != null) {
            onEntrySaved(uiState.savedEntryId!!)
            viewModel.resetState() // Reset to avoid double navigation
        }
    }

    // Handle Error Toast
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    WriteEntryContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onContentChange = viewModel::updateContent,
        onSaveClick = viewModel::saveEntry
    )
}

@Composable
fun WriteEntryContent(
    uiState: WriteUiState,
    onNavigateBack: () -> Unit,
    onContentChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MinderBackground,
            topBar = {
                WriteEntryTopBar(
                    onBackClick = onNavigateBack,
                    isSaving = uiState.isSaving,
                    isEditMode = uiState.isEditMode
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OutlinedDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .height(4.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Input Area
                    NeoShadowBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        containerColor = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            if (uiState.content.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.write_hint),
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        color = Color.Gray,
                                        lineHeight = 26.sp
                                    )
                                )
                            }
                            
                            BasicTextField(
                                value = uiState.content,
                                onValueChange = onContentChange,
                                modifier = Modifier.fillMaxSize(),
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    lineHeight = 26.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                cursorBrush = SolidColor(Color.Black),
                                enabled = !uiState.isSaving
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Action Button
                    NeoShadowBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clickable(
                                enabled = !uiState.isSaving && uiState.content.isNotBlank(),
                                onClick = onSaveClick
                            ),
                        containerColor = if (uiState.isSaving) Color.Gray else MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (uiState.isSaving) {
                                 Row(verticalAlignment = Alignment.CenterVertically) {
                                     RetroLoadingIndicator(modifier = Modifier.size(24.dp))
                                     Spacer(modifier = Modifier.width(8.dp))
                                     Text(
                                         text = stringResource(R.string.write_saving),
                                         color = Color.White,
                                         fontWeight = FontWeight.Bold
                                     )
                                 }
                            } else {
                                Text(
                                    text = stringResource(R.string.write_save_button),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Blocking Overlay (Covers everything including TopBar)
        BlockingLoadingOverlay(
            isVisible = uiState.isSaving,
            message = stringResource(R.string.write_saving)
        )
    }
}

@Composable
fun WriteEntryTopBar(
    onBackClick: () -> Unit,
    isSaving: Boolean,
    isEditMode: Boolean
) {
    Row(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RetroIconButton(
            onClick = onBackClick,
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.common_back)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = stringResource(if (isEditMode) R.string.write_entry_title_edit else R.string.write_entry_title),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WriteEntryScreenPreview() {
    MinderTheme {
        WriteEntryContent(
            uiState = WriteUiState(content = ""),
            onNavigateBack = {},
            onContentChange = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WriteEntryScreenLoadingPreview() {
    MinderTheme {
        WriteEntryContent(
            uiState = WriteUiState(content = "오늘 하루는 정말 기분 좋은 날이었다.", isSaving = true),
            onNavigateBack = {},
            onContentChange = {},
            onSaveClick = {}
        )
    }
}
