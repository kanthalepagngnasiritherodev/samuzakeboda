package com.example.ui.keyboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.WijesekaraLayout

@Composable
fun WijesekaraKeyboardLayout(
    state: KeyboardUiState,
    onChar: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onAction: () -> Unit,
    onToggleShift: () -> Unit,
    onSwitchToAlpha: () -> Unit,
    onSwitchToNumbers: () -> Unit,
    onSwitchToEmoji: () -> Unit
) {
    val row1 = if (state.isShifted) WijesekaraLayout.shiftedRow1 else WijesekaraLayout.normalRow1
    val row2 = if (state.isShifted) WijesekaraLayout.shiftedRow2 else WijesekaraLayout.normalRow2
    val row3 = if (state.isShifted) WijesekaraLayout.shiftedRow3 else WijesekaraLayout.normalRow3

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row1.forEach { (_, sinhala) ->
                Keycap(
                    label = sinhala,
                    modifier = Modifier.weight(1f),
                    onClick = { onChar(sinhala) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            row2.forEach { (_, sinhala) ->
                Keycap(
                    label = sinhala,
                    modifier = Modifier.weight(1f),
                    onClick = { onChar(sinhala) }
                )
            }
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpecialKeycap(
                modifier = Modifier.weight(1.4f),
                onClick = onToggleShift,
                isActive = state.isShifted,
                activeColor = NeonCyan
            ) {
                Text(
                    text = "Shift",
                    color = if (state.isShifted) NeonCyan else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            row3.forEach { (_, sinhala) ->
                Keycap(
                    label = sinhala,
                    modifier = Modifier.weight(1f),
                    onClick = { onChar(sinhala) }
                )
            }

            SpecialKeycap(
                modifier = Modifier.weight(1.4f),
                onClick = onDelete
            ) {
                Text(text = "⌫", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpecialKeycap(
                modifier = Modifier.weight(1.2f),
                onClick = onSwitchToAlpha
            ) {
                Text(text = "EN", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            SpecialKeycap(
                modifier = Modifier.weight(1.2f),
                onClick = onSwitchToNumbers
            ) {
                Text(text = "?123", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            SpecialKeycap(
                modifier = Modifier.weight(0.9f),
                onClick = onSwitchToEmoji
            ) {
                Text(text = "😊", fontSize = 16.sp)
            }

            Keycap(
                label = "විජේසේකර Space",
                modifier = Modifier.weight(4.0f),
                fontSize = 12.sp,
                isAccented = true,
                accentColor = NeonPurple,
                onClick = onSpace
            )

            SpecialKeycap(
                modifier = Modifier.weight(1.3f),
                onClick = onAction,
                isActive = true,
                activeColor = NeonCyan
            ) {
                Text(text = "Go ↵", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TextEditingDrawer(
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onCopy: () -> Unit,
    onCut: () -> Unit,
    onPaste: () -> Unit,
    onSelectAll: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xEB13192B),
        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
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
                        imageVector = Icons.Default.TextFields,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Text Editing & Cursor Controls",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action row: Select All, Cut, Copy, Paste
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    onClick = onSelectAll,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.SelectAll, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "All", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    onClick = onCut,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ContentCut, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Cut", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    onClick = onCopy,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Copy", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Surface(
                    onClick = onPaste,
                    shape = RoundedCornerShape(10.dp),
                    color = NeonGreen.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.ContentPaste, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Paste", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Directional Pad (D-pad arrows)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onMoveLeft,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.size(width = 54.dp, height = 36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        onClick = onMoveUp,
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(width = 54.dp, height = 36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White)
                        }
                    }
                    Surface(
                        onClick = onMoveDown,
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(width = 54.dp, height = 36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    onClick = onMoveRight,
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    modifier = Modifier.size(width = 54.dp, height = 36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun KeyboardQuickSettingsDrawer(
    state: KeyboardUiState,
    onToggleSound: () -> Unit,
    onToggleVibration: () -> Unit,
    onToggleNumberRow: () -> Unit,
    onToggleOneHanded: () -> Unit,
    onChangeScale: (Float) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color(0xEB13192B),
        border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.4f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Keyboard Settings & Personalization", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sound and Vibration toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Keypress Click Sound", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                Switch(
                    checked = state.isSoundEnabled,
                    onCheckedChange = { onToggleSound() },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Keypress Vibration (Haptics)", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                Switch(
                    checked = state.isVibrationEnabled,
                    onCheckedChange = { onToggleVibration() },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Top Number Row", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                Switch(
                    checked = state.isNumberRowEnabled,
                    onCheckedChange = { onToggleNumberRow() },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "One-Handed Mode", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                Switch(
                    checked = state.isOneHandedMode,
                    onCheckedChange = { onToggleOneHanded() },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Height Resize options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Keyboard Height:", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(0.85f to "Compact", 1.0f to "Normal", 1.15f to "Tall").forEach { (scale, name) ->
                        Surface(
                            onClick = { onChangeScale(scale) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (state.keyboardScaleHeight == scale) NeonCyan else Color.White.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = name,
                                color = if (state.keyboardScaleHeight == scale) Color.Black else Color.White,
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
