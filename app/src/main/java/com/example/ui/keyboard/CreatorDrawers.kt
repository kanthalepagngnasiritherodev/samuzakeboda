package com.example.ui.keyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.FancyStyle
import com.example.util.FancyTextGenerator
import com.example.util.HashtagVault

@Composable
fun FancyFontsMiniDrawer(
    state: KeyboardUiState,
    onSelectTypingStyle: (styleId: String, enableTyping: Boolean) -> Unit,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit
) {
    var previewInput by remember { mutableStateOf("SamuZA Creator") }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xEB111827),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Fancy Text Styles",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Active typing toggle pill
                    Surface(
                        onClick = {
                            onSelectTypingStyle(state.selectedFancyStyleId, !state.isFancyTextEnabled)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = if (state.isFancyTextEnabled) NeonCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, if (state.isFancyTextEnabled) NeonCyan else Color.White.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (state.isFancyTextEnabled) "Typing: ON" else "Typing: OFF",
                                color = if (state.isFancyTextEnabled) NeonCyan else Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Caption Test Field
            OutlinedTextField(
                value = previewInput,
                onValueChange = { previewInput = it },
                placeholder = { Text("Type custom text to style...", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Styles list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 140.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(FancyTextGenerator.styles) { style ->
                    val styledPreview = remember(style, previewInput) {
                        style.converter(if (previewInput.isNotEmpty()) previewInput else "SamuZA")
                    }
                    val isCurrentActive = state.isFancyTextEnabled && state.selectedFancyStyleId == style.id

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrentActive) NeonCyan.copy(alpha = 0.15f) else Color(0x331F293D),
                        border = BorderStroke(1.dp, if (isCurrentActive) NeonCyan else Color.White.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = style.name,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = styledPreview,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                // "Use" for typing button
                                Surface(
                                    onClick = {
                                        onSelectTypingStyle(style.id, true)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCurrentActive) NeonCyan else Color.White.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = if (isCurrentActive) "Active" else "Use",
                                        color = if (isCurrentActive) Color.Black else Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                // "Insert" into text button
                                Surface(
                                    onClick = {
                                        onInsertText(styledPreview)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = NeonPurple.copy(alpha = 0.3f),
                                    border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "Insert",
                                        color = NeonPurple,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashtagVaultMiniDrawer(
    onInsertHashtag: (String) -> Unit,
    onInsertEmoji: (String) -> Unit,
    onClose: () -> Unit
) {
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    val currentCategory = HashtagVault.categories.getOrElse(selectedCategoryIndex) { HashtagVault.categories.first() }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xEB111827),
        border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hashtag & Emoji Vault",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // "Insert All" button for category
                    Surface(
                        onClick = {
                            val cluster = currentCategory.hashtags.joinToString(" ") + " "
                            onInsertHashtag(cluster)
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = NeonAmber.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "+ Insert All",
                            color = NeonAmber,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onClose, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Category selector tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(HashtagVault.categories.indices.toList()) { index ->
                    val cat = HashtagVault.categories[index]
                    val isSelected = selectedCategoryIndex == index
                    Surface(
                        onClick = { selectedCategoryIndex = index },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) NeonAmber.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.07f),
                        border = BorderStroke(1.dp, if (isSelected) NeonAmber else Color.White.copy(alpha = 0.12f))
                    ) {
                        Text(
                            text = "${cat.iconEmoji} ${cat.title}",
                            color = if (isSelected) NeonAmber else Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hashtag Chips Flow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 86.dp)
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    currentCategory.hashtags.forEach { tag ->
                        Surface(
                            onClick = { onInsertHashtag("$tag ") },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x33252F47),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                        ) {
                            Text(
                                text = tag,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Creator Hype Emojis Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Top Emojis:",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(HashtagVault.creatorEmojis) { emoji ->
                        Surface(
                            onClick = { onInsertEmoji(emoji) },
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.08f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = emoji, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SinglishComposingCandidateBar(
    singlishBuffer: String,
    sinhalaPreview: String,
    onAccept: () -> Unit
) {
    Surface(
        onClick = onAccept,
        shape = RoundedCornerShape(14.dp),
        color = NeonPurple.copy(alpha = 0.22f),
        border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.6f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Singlish: ",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp
                )
                Text(
                    text = "$singlishBuffer  ➔  ",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = sinhalaPreview,
                    color = NeonCyan,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NeonPurple,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Accept",
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Accept",
                        color = Color.Black,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
