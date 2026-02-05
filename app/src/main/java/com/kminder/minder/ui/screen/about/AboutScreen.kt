package com.kminder.minder.ui.screen.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kminder.minder.BuildConfig
import com.kminder.minder.R
import com.kminder.minder.ui.component.NeoShadowBox
import com.kminder.minder.ui.component.OutlinedDivider
import com.kminder.minder.ui.screen.list.RetroIconButton
import com.kminder.minder.ui.theme.MinderBackground
import com.kminder.minder.ui.theme.MinderTheme

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AboutTopBar(onBackClick = onNavigateBack)
        },
        containerColor = MinderBackground
    ) { paddingValues ->
        AboutContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun AboutTopBar(onBackClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RetroIconButton(
                onClick = onBackClick,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.common_back)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(R.string.about_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
        }

        OutlinedDivider(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun AboutContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    CompositionLocalProvider(
        LocalOverscrollFactory provides null
    ) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Icon & Name
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(3.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
            }

            Text(
                text = stringResource(R.string.about_app_name),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                color = Color.Black
            )

            Text(
                text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            // Developer's Note
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp) // Adjusted min height for better initial view
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(16.dp)) // Neo-Brutalism border
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DeveloperMessage()
                }
            }

            // Contact Section'
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp) // Adjusted min height for better initial view
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(16.dp)) // Neo-Brutalism border
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.about_contact_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )

                    // Email
                    ContactItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        },
                        label = stringResource(R.string.about_email),
                        value = "contact@minder.app",
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:contact@minder.app")
                            }
                            context.startActivity(intent)
                        }
                    )

                    // GitHub
                    ContactItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = stringResource(R.string.about_github),
                        value = "github.com/minder",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/minder"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            // Copyright
            Text(
                text = stringResource(R.string.about_copyright),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
fun DeveloperMessage() {
    val context = LocalContext.current
    val isKorean = context.resources.configuration.locales[0].language == "ko"
    var message by remember { mutableStateOf("") }

    LaunchedEffect(isKorean) {
        val fileName = if (isKorean) "development_message_ko.md" else "development_message_en.md"
        message = try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Developer message unavailable."
        }
    }
    
    val annotatedString = buildAnnotatedString {
        val boldRegex = "\\*\\*(.*?)\\*\\*".toRegex()
        var lastIndex = 0
        
        boldRegex.findAll(message).forEach { match ->
            // Append text before match
            append(message.substring(lastIndex, match.range.first))
            
            // Apply bold style to captured group (content inside **)
            pushStyle(androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold))
            append(match.groupValues[1])
            pop()
            
            lastIndex = match.range.last + 1
        }
        
        // Append remaining text
        if (lastIndex < message.length) {
            append(message.substring(lastIndex))
        }
    }
    
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        color = Color.DarkGray,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.5f)
    )
}

@Composable
fun ContactItem(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = Color.Black
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    MinderTheme {
        AboutScreen(onNavigateBack = {})
    }
}
