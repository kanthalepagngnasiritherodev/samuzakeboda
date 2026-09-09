package com.example.ui.keyboard

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.clipboard.ClipboardHistoryManager
import com.example.privacy.SecretMessageCipher
import com.example.shortcuts.TextExpansionManager
import com.example.shortcuts.TextShortcut
import com.example.status.StatusItem
import com.example.status.StatusMediaManager
import com.example.util.FancyTextGenerator
import com.example.util.HashtagVault
import com.example.util.SinglishConverter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// iOS 18 Glassmorphism Dark Theme Palette
val GlassCanvasDark = Color(0xFF0B0E17)
val GlassSurfaceDark = Color(0xCC161F30)
val GlassKeyDark = Color(0x402A3854)
val GlassKeyAccent = Color(0x60374768)
val GlassKeyBorder = Color(0x33A0B9E5)
val GlassHighlight = Color(0x26FFFFFF)
val NeonCyan = Color(0xFF00E5FF)
val NeonPurple = Color(0xFFA855F7)
val NeonGreen = Color(0xFF10B981)
val NeonAmber = Color(0xFFF59E0B)

interface KeyboardActionListener {
    fun onCommitText(text: String)
    fun onSetComposingText(text: String)
    fun onFinishComposingText()
    fun onDelete()
    fun onAction()
    fun onSwitchIme()
    fun onHide()
}

@Composable
fun SamuZaKeyboardView(
    listener: KeyboardActionListener,
    modifier: Modifier = Modifier,
    isEmbeddedPreview: Boolean = false,
    onOpenSettings: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var state by remember { mutableStateOf(KeyboardUiState()) }
    var clipboardItems by remember { mutableStateOf(emptyList<String>()) }
    var shortcutsList by remember { mutableStateOf(emptyList<TextShortcut>()) }
    var statusesList by remember { mutableStateOf(emptyList<StatusItem>()) }
    var isScanningStatuses by remember { mutableStateOf(false) }

    // Load initial data
    LaunchedEffect(Unit) {
        ClipboardHistoryManager.init(context)
        clipboardItems = ClipboardHistoryManager.getItems(context)
        shortcutsList = TextExpansionManager.getShortcuts(context)
        statusesList = StatusMediaManager.getStatuses(context)

        // Check if current clipboard has a secret message
        checkClipboardForSecrets(context) { plain, cipher ->
            state = state.copy(
                detectedSecretMessage = cipher,
                decodedClipboardText = plain
            )
        }
    }

    // Refresh when drawers open
    LaunchedEffect(state.activeDrawer) {
        if (state.activeDrawer == ActiveDrawer.CLIPBOARD) {
            clipboardItems = ClipboardHistoryManager.getItems(context)
        } else if (state.activeDrawer == ActiveDrawer.SHORTCUTS) {
            shortcutsList = TextExpansionManager.getShortcuts(context)
        } else if (state.activeDrawer == ActiveDrawer.WHATSAPP_STATUSES) {
            statusesList = StatusMediaManager.getStatuses(context)
        }
    }

    fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(20)
                }
            }
        } catch (e: Exception) {
            // ignore haptic failure
        }
    }

    fun handleCommit(text: String) {
        triggerHaptic()
        val textToCommit = when {
            state.isSecretChatEnabled -> {
                SecretMessageCipher.encode(text)
            }
            state.isFancyTextEnabled -> {
                FancyTextGenerator.convert(text, state.selectedFancyStyleId)
            }
            else -> text
        }
        listener.onCommitText(textToCommit)
    }

    fun handleCharTyped(char: String) {
        triggerHaptic()
        if (state.isSecretChatEnabled) {
            val encoded = SecretMessageCipher.encode(char)
            listener.onCommitText(encoded)
            return
        }
        if (state.isFancyTextEnabled) {
            val fancy = FancyTextGenerator.convert(char, state.selectedFancyStyleId)
            listener.onCommitText(fancy)
            return
        }
        if (state.isSinglishEnabled) {
            val newBuf = state.activeSinglishBuffer + char
            val converted = SinglishConverter.convertWord(newBuf)
            state = state.copy(activeSinglishBuffer = newBuf, activeSinhalaPreview = converted)
            listener.onSetComposingText(converted)
            return
        }
        listener.onCommitText(char)
    }

    fun handleDelete() {
        triggerHaptic()
        if (state.isSinglishEnabled && state.activeSinglishBuffer.isNotEmpty()) {
            val newBuf = state.activeSinglishBuffer.dropLast(1)
            if (newBuf.isEmpty()) {
                state = state.copy(activeSinglishBuffer = "", activeSinhalaPreview = "")
                listener.onSetComposingText("")
                listener.onFinishComposingText()
            } else {
                val converted = SinglishConverter.convertWord(newBuf)
                state = state.copy(activeSinglishBuffer = newBuf, activeSinhalaPreview = converted)
                listener.onSetComposingText(converted)
            }
        } else {
            listener.onDelete()
        }
    }

    fun handleSpace() {
        triggerHaptic()
        if (state.isSinglishEnabled && state.activeSinglishBuffer.isNotEmpty()) {
            val expansion = TextExpansionManager.findExpansion(context, state.activeSinglishBuffer)
            listener.onFinishComposingText()
            if (expansion != null) {
                listener.onCommitText(expansion)
            }
            state = state.copy(activeSinglishBuffer = "", activeSinhalaPreview = "")
            listener.onCommitText(" ")
            return
        }
        listener.onCommitText(" ")
    }

    fun handleAction() {
        triggerHaptic()
        if (state.isSinglishEnabled && state.activeSinglishBuffer.isNotEmpty()) {
            listener.onFinishComposingText()
            state = state.copy(activeSinglishBuffer = "", activeSinhalaPreview = "")
        }
        listener.onAction()
    }

    fun showFeedback(msg: String) {
        state = state.copy(feedbackMessage = msg)
        coroutineScope.launch {
            delay(2000)
            if (state.feedbackMessage == msg) {
                state = state.copy(feedbackMessage = null)
            }
        }
    }

    // 32px rounded container styling with iOS 18 glassmorphic dark finish
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141926),
                        Color(0xFF090D15),
                        Color(0xFF05070B)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.White.copy(alpha = 0.04f)
                        )
                    )
                ),
                RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(top = 10.dp, bottom = 12.dp, start = 8.dp, end = 8.dp)
            .testTag("samuza_keyboard_container")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glass Specular Bar (iOS 18 handle style)
            Box(
                modifier = Modifier
                    .size(width = 38.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f))
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Decoded Clipboard Banner (Auto Secret Detector)
            AnimatedVisibility(
                visible = state.decodedClipboardText != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                state.decodedClipboardText?.let { decodedText ->
                    DecodedSecretNotificationBanner(
                        decodedText = decodedText,
                        onInsert = {
                            listener.onCommitText(decodedText)
                            state = state.copy(decodedClipboardText = null)
                            showFeedback("Decoded message inserted!")
                        },
                        onDismiss = {
                            state = state.copy(decodedClipboardText = null)
                        }
                    )
                }
            }

            // Feedback toast-like chip
            AnimatedVisibility(
                visible = state.feedbackMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.feedbackMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NeonCyan.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.6f)),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = msg,
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Top Smart Utility Toolbar
            KeyboardTopUtilityToolbar(
                state = state,
                statusCount = statusesList.size,
                onToggleStatuses = {
                    triggerHaptic()
                    state = state.copy(
                        activeDrawer = if (state.activeDrawer == ActiveDrawer.WHATSAPP_STATUSES) ActiveDrawer.NONE else ActiveDrawer.WHATSAPP_STATUSES
                    )
                },
                onToggleSecretChat = {
                    triggerHaptic()
                    val newMode = !state.isSecretChatEnabled
                    state = state.copy(
                        isSecretChatEnabled = newMode,
                        activeDrawer = if (newMode) ActiveDrawer.SECRET_CHAT else ActiveDrawer.NONE
                    )
                    showFeedback(if (newMode) "🔒 Secret Chat Enabled! (Auto-Encrypt)" else "Secret Chat Disabled")
                },
                onToggleSinglish = {
                    triggerHaptic()
                    val newMode = !state.isSinglishEnabled
                    state = state.copy(isSinglishEnabled = newMode)
                    showFeedback(if (newMode) "සිං Sinhala / Singlish Input ON" else "English Input ON")
                },
                onToggleFancyFonts = {
                    triggerHaptic()
                    state = state.copy(
                        activeDrawer = if (state.activeDrawer == ActiveDrawer.FANCY_FONTS) ActiveDrawer.NONE else ActiveDrawer.FANCY_FONTS
                    )
                },
                onToggleHashtags = {
                    triggerHaptic()
                    state = state.copy(
                        activeDrawer = if (state.activeDrawer == ActiveDrawer.HASHTAG_VAULT) ActiveDrawer.NONE else ActiveDrawer.HASHTAG_VAULT
                    )
                },
                onToggleClipboard = {
                    triggerHaptic()
                    state = state.copy(
                        activeDrawer = if (state.activeDrawer == ActiveDrawer.CLIPBOARD) ActiveDrawer.NONE else ActiveDrawer.CLIPBOARD
                    )
                },
                onToggleShortcuts = {
                    triggerHaptic()
                    state = state.copy(
                        activeDrawer = if (state.activeDrawer == ActiveDrawer.SHORTCUTS) ActiveDrawer.NONE else ActiveDrawer.SHORTCUTS
                    )
                },
                onSwitchKeyboard = {
                    triggerHaptic()
                    listener.onSwitchIme()
                },
                onHideKeyboard = {
                    triggerHaptic()
                    listener.onHide()
                },
                onOpenSettings = onOpenSettings
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Active Drawers (WhatsApp Status Saver / Secret Chat / Clipboard / Shortcuts / Fancy Fonts / Hashtag Vault)
            AnimatedVisibility(
                visible = state.activeDrawer != ActiveDrawer.NONE,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                when (state.activeDrawer) {
                    ActiveDrawer.WHATSAPP_STATUSES -> {
                        WhatsAppStatusMiniDrawer(
                            statuses = statusesList,
                            isScanning = isScanningStatuses,
                            onRefresh = {
                                isScanningStatuses = true
                                coroutineScope.launch {
                                    statusesList = StatusMediaManager.getStatuses(context)
                                    delay(400)
                                    isScanningStatuses = false
                                    showFeedback("Found ${statusesList.size} statuses")
                                }
                            },
                            onSave = { status ->
                                triggerHaptic()
                                val success = StatusMediaManager.saveStatusToGallery(context, status)
                                if (success) {
                                    showFeedback("Saved to Gallery! 📥")
                                } else {
                                    showFeedback("Failed to save status")
                                }
                            },
                            onClose = {
                                state = state.copy(activeDrawer = ActiveDrawer.NONE)
                            }
                        )
                    }
                    ActiveDrawer.FANCY_FONTS -> {
                        FancyFontsMiniDrawer(
                            state = state,
                            onSelectTypingStyle = { styleId, enableTyping ->
                                triggerHaptic()
                                state = state.copy(
                                    isFancyTextEnabled = enableTyping,
                                    selectedFancyStyleId = styleId
                                )
                                val styleName = FancyTextGenerator.styles.find { it.id == styleId }?.name ?: "Fancy"
                                showFeedback(if (enableTyping) "✨ Typing in $styleName style" else "Normal typing restored")
                            },
                            onInsertText = { styledText ->
                                triggerHaptic()
                                listener.onCommitText(styledText)
                                showFeedback("Inserted styled text!")
                            },
                            onClose = {
                                state = state.copy(activeDrawer = ActiveDrawer.NONE)
                            }
                        )
                    }
                    ActiveDrawer.HASHTAG_VAULT -> {
                        HashtagVaultMiniDrawer(
                            onInsertHashtag = { tag ->
                                triggerHaptic()
                                listener.onCommitText(tag)
                                showFeedback("Inserted hashtag!")
                            },
                            onInsertEmoji = { emoji ->
                                triggerHaptic()
                                listener.onCommitText(emoji)
                            },
                            onClose = {
                                state = state.copy(activeDrawer = ActiveDrawer.NONE)
                            }
                        )
                    }
                    ActiveDrawer.SECRET_CHAT -> {
                        SecretChatDrawer(
                            state = state,
                            onEncryptText = { textToEncrypt ->
                                triggerHaptic()
                                val encoded = SecretMessageCipher.encode(textToEncrypt)
                                listener.onCommitText(encoded)
                                ClipboardHistoryManager.addItem(context, encoded)
                                showFeedback("Encrypted text committed! 🔒")
                            },
                            onClose = {
                                state = state.copy(activeDrawer = ActiveDrawer.NONE)
                            }
                        )
                    }
                    ActiveDrawer.CLIPBOARD -> {
                        ClipboardHistoryMiniDrawer(
                            items = clipboardItems,
                            onPaste = { text ->
                                triggerHaptic()
                                listener.onCommitText(text)
                                showFeedback("Pasted from clipboard")
                            },
                            onDelete = { text ->
                                ClipboardHistoryManager.removeItem(context, text)
                                clipboardItems = ClipboardHistoryManager.getItems(context)
                            },
                            onClearAll = {
                                ClipboardHistoryManager.clearAll(context)
                                clipboardItems = emptyList()
                                showFeedback("Clipboard history cleared")
                            },
                            onClose = {
                                state = state.copy(activeDrawer = ActiveDrawer.NONE)
                            }
                        )
                    }
                    ActiveDrawer.SHORTCUTS -> {
                        ShortcutsMiniDrawer(
                            shortcuts = shortcutsList,
                            onInsert = { shortcut ->
                                triggerHaptic()
                                listener.onCommitText(shortcut.expandedText)
                                showFeedback("Expanded: ${shortcut.trigger}")
                            },
                            onClose = {
                                state = state.copy(activeDrawer = ActiveDrawer.NONE)
                            }
                        )
                    }
                    ActiveDrawer.NONE -> Unit
                }
            }

            // Singlish Candidate Live Transliteration Strip
            AnimatedVisibility(
                visible = state.isSinglishEnabled && state.activeSinglishBuffer.isNotEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                SinglishComposingCandidateBar(
                    singlishBuffer = state.activeSinglishBuffer,
                    sinhalaPreview = state.activeSinhalaPreview,
                    onAccept = {
                        triggerHaptic()
                        listener.onFinishComposingText()
                        state = state.copy(activeSinglishBuffer = "", activeSinhalaPreview = "")
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main Keypad Views
            when (state.layout) {
                KeyboardLayout.ALPHA -> {
                    AlphaKeyboardLayout(
                        state = state,
                        onChar = { char ->
                            handleCharTyped(char)
                            if (state.isShifted && !state.isCapsLock) {
                                state = state.copy(isShifted = false)
                            }
                        },
                        onDelete = { handleDelete() },
                        onSpace = { handleSpace() },
                        onAction = { handleAction() },
                        onToggleShift = {
                            triggerHaptic()
                            state = if (!state.isShifted) {
                                state.copy(isShifted = true, isCapsLock = false)
                            } else if (!state.isCapsLock) {
                                state.copy(isShifted = true, isCapsLock = true)
                            } else {
                                state.copy(isShifted = false, isCapsLock = false)
                            }
                        },
                        onSwitchToNumbers = {
                            triggerHaptic()
                            state = state.copy(layout = KeyboardLayout.NUMBERS)
                        },
                        onSwitchToEmoji = {
                            triggerHaptic()
                            state = state.copy(layout = KeyboardLayout.EMOJI)
                        }
                    )
                }
                KeyboardLayout.NUMBERS -> {
                    NumericSymbolKeyboardLayout(
                        state = state,
                        isSecondarySymbols = false,
                        onChar = { char -> handleCharTyped(char) },
                        onDelete = { handleDelete() },
                        onSpace = { handleSpace() },
                        onAction = { handleAction() },
                        onSwitchToAlpha = {
                            triggerHaptic()
                            state = state.copy(layout = KeyboardLayout.ALPHA)
                        },
                        onSwitchToMoreSymbols = {
                            triggerHaptic()
                            state = state.copy(layout = KeyboardLayout.SYMBOLS)
                        }
                    )
                }
                KeyboardLayout.SYMBOLS -> {
                    NumericSymbolKeyboardLayout(
                        state = state,
                        isSecondarySymbols = true,
                        onChar = { char -> handleCharTyped(char) },
                        onDelete = { handleDelete() },
                        onSpace = { handleSpace() },
                        onAction = { handleAction() },
                        onSwitchToAlpha = {
                            triggerHaptic()
                            state = state.copy(layout = KeyboardLayout.ALPHA)
                        },
                        onSwitchToMoreSymbols = {
                            triggerHaptic()
                            state = state.copy(layout = KeyboardLayout.NUMBERS)
                        }
                    )
                }
                KeyboardLayout.EMOJI -> {
                    EmojiKeyboardLayout(
                        onEmojiSelected = { emoji -> handleCommit(emoji) },
                        onSwitchToAlpha = {
                            triggerHaptic()
                            state = state.copy(layout = KeyboardLayout.ALPHA)
                        },
                        onDelete = { handleDelete() }
                    )
                }
            }
        }
    }
}

@Composable
fun KeyboardTopUtilityToolbar(
    state: KeyboardUiState,
    statusCount: Int,
    onToggleStatuses: () -> Unit,
    onToggleSecretChat: () -> Unit,
    onToggleSinglish: () -> Unit,
    onToggleFancyFonts: () -> Unit,
    onToggleHashtags: () -> Unit,
    onToggleClipboard: () -> Unit,
    onToggleShortcuts: () -> Unit,
    onSwitchKeyboard: () -> Unit,
    onHideKeyboard: () -> Unit,
    onOpenSettings: (() -> Unit)?
) {
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Singlish to Sinhala Input Mode
        item {
            ToolbarPill(
                icon = Icons.Default.Translate,
                label = if (state.isSinglishEnabled) "සිං Sinhala" else "English",
                isActive = state.isSinglishEnabled,
                activeColor = NeonPurple,
                onClick = onToggleSinglish,
                testTag = "toolbar_singlish_toggle"
            )
        }

        // Fancy Text Style Generator
        item {
            ToolbarPill(
                icon = Icons.Default.AutoAwesome,
                label = if (state.isFancyTextEnabled) "Fancy 𝗕" else "Fonts",
                isActive = state.activeDrawer == ActiveDrawer.FANCY_FONTS || state.isFancyTextEnabled,
                activeColor = NeonCyan,
                onClick = onToggleFancyFonts,
                testTag = "toolbar_fancy_fonts"
            )
        }

        // Hashtag Vault
        item {
            ToolbarPill(
                icon = Icons.Default.Tag,
                label = "#Tags",
                isActive = state.activeDrawer == ActiveDrawer.HASHTAG_VAULT,
                activeColor = NeonAmber,
                onClick = onToggleHashtags,
                testTag = "toolbar_hashtag_vault"
            )
        }

        // Quick Snippets / Templates
        item {
            ToolbarPill(
                icon = Icons.Default.FlashOn,
                label = "Templates",
                isActive = state.activeDrawer == ActiveDrawer.SHORTCUTS,
                activeColor = Color(0xFF38BDF8),
                onClick = onToggleShortcuts,
                testTag = "toolbar_shortcuts"
            )
        }

        // WhatsApp Status Saver Pill
        item {
            ToolbarPill(
                icon = Icons.Default.VideoLibrary,
                label = "Statuses",
                isActive = state.activeDrawer == ActiveDrawer.WHATSAPP_STATUSES,
                activeColor = NeonGreen,
                badgeCount = statusCount,
                onClick = onToggleStatuses,
                testTag = "toolbar_status_saver"
            )
        }

        // Secret Chat Privacy Button
        item {
            ToolbarPill(
                icon = if (state.isSecretChatEnabled) Icons.Default.Lock else Icons.Default.LockOpen,
                label = if (state.isSecretChatEnabled) "Secret ON" else "Secret",
                isActive = state.isSecretChatEnabled,
                activeColor = Color(0xFFEF4444),
                onClick = onToggleSecretChat,
                testTag = "toolbar_secret_chat"
            )
        }

        // Clipboard Manager
        item {
            ToolbarPill(
                icon = Icons.Default.ContentCopy,
                label = "Clip",
                isActive = state.activeDrawer == ActiveDrawer.CLIPBOARD,
                activeColor = Color(0xFFA855F7),
                onClick = onToggleClipboard,
                testTag = "toolbar_clipboard"
            )
        }

        // Switch IME button
        item {
            IconButton(
                onClick = onSwitchKeyboard,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = "Switch Input Method",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(17.dp)
                )
            }
        }

        // Hide keyboard button
        item {
            IconButton(
                onClick = onHideKeyboard,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardHide,
                    contentDescription = "Hide Keyboard",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(17.dp)
                )
            }
        }
    }
}

@Composable
fun ToolbarPill(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    testTag: String = ""
) {
    val bgColor by animateColorAsState(
        targetValue = if (isActive) activeColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.06f),
        label = "pill_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isActive) activeColor else Color.White.copy(alpha = 0.12f),
        label = "pill_border"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isActive) activeColor else Color.White.copy(alpha = 0.85f),
        label = "pill_color"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .height(34.dp)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium
            )
            if (badgeCount > 0) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(NeonGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$badgeCount",
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WhatsAppStatusMiniDrawer(
    statuses: List<StatusItem>,
    isScanning: Boolean,
    onRefresh: () -> Unit,
    onSave: (StatusItem) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xEB111827),
        border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.35f)),
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
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = NeonGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "WhatsApp Status Saver",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${statuses.size} available)",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }
                Row {
                    IconButton(onClick = onRefresh, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Scan Statuses",
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
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

            if (statuses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No statuses found. Open WhatsApp to view statuses, then tap Refresh.",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(statuses) { status ->
                        StatusThumbnailCard(status = status, onSave = { onSave(status) })
                    }
                }
            }
        }
    }
}

@Composable
fun StatusThumbnailCard(
    status: StatusItem,
    onSave: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0x3320293E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
        modifier = Modifier
            .size(width = 90.dp, height = 110.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (status.uri != null) {
                AsyncImage(
                    model = status.uri,
                    contentDescription = status.displayName,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Gradient placeholder for sample
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                if (status.isVideo) listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
                                else listOf(Color(0xFF065F46), Color(0xFF10B981))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (status.isVideo) Icons.Default.PlayArrow else Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Top badge if video
            if (status.isVideo) {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(text = "VID", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Save to gallery button overlay
            Surface(
                onClick = onSave,
                shape = CircleShape,
                color = NeonGreen,
                modifier = Modifier
                    .size(28.dp)
                    .padding(2.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save Status to Gallery",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SecretChatDrawer(
    state: KeyboardUiState,
    onEncryptText: (String) -> Unit,
    onClose: () -> Unit
) {
    var quickSecretInput by remember { mutableStateOf("") }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xEB1A1528),
        border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.45f)),
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
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Secret Chat Privacy Tool",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Type any message or use keyboard directly: text automatically encrypts into 🔒SZ::...::🔒 token!",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Quick presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Meet at 5 PM", "Secret OTP: 9812", "Send Account Details", "Call me privately").forEach { phrase ->
                    Surface(
                        onClick = { onEncryptText(phrase) },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "🔒 $phrase",
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClipboardHistoryMiniDrawer(
    items: List<String>,
    onPaste: (String) -> Unit,
    onDelete: (String) -> Unit,
    onClearAll: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xEB0D1527),
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
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Clipboard History (${items.size}/20)",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Row {
                    if (items.isNotEmpty()) {
                        IconButton(onClick = onClearAll, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear All",
                                tint = Color.Red.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
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

            if (items.isEmpty()) {
                Text(
                    text = "Clipboard is empty. Copy any text from any app to access it here.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(items) { clipText ->
                        val isEncrypted = SecretMessageCipher.isSecretMessage(clipText)
                        val decodedPreview = if (isEncrypted) SecretMessageCipher.decode(clipText) else null

                        Surface(
                            onClick = { onPaste(clipText) },
                            shape = RoundedCornerShape(14.dp),
                            color = if (isEncrypted) NeonAmber.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.08f),
                            border = BorderStroke(
                                1.dp,
                                if (isEncrypted) NeonAmber.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.width(130.dp)) {
                                    if (isEncrypted && decodedPreview != null) {
                                        Text(
                                            text = "🔓 $decodedPreview",
                                            color = NeonAmber,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Encrypted SamuZA Token",
                                            color = Color.White.copy(alpha = 0.5f),
                                            fontSize = 9.sp,
                                            maxLines = 1
                                        )
                                    } else {
                                        Text(
                                            text = clipText,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { onDelete(clipText) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = Color.White.copy(alpha = 0.4f),
                                        modifier = Modifier.size(12.dp)
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

@Composable
fun ShortcutsMiniDrawer(
    shortcuts: List<TextShortcut>,
    onInsert: (TextShortcut) -> Unit,
    onClose: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color(0xEB131F33),
        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f)),
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
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quick Text Expansion",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(shortcuts) { sc ->
                    Surface(
                        onClick = { onInsert(sc) },
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f)),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "!${sc.trigger}",
                                color = Color(0xFF38BDF8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = sc.expandedText,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(110.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DecodedSecretNotificationBanner(
    decodedText: String,
    onInsert: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xEB271708),
        border = BorderStroke(1.dp, NeonAmber),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LockOpen,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Encrypted Message Auto-Decoded:",
                        color = NeonAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = decodedText,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Row {
                Surface(
                    onClick = onInsert,
                    shape = RoundedCornerShape(12.dp),
                    color = NeonAmber,
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Text(
                        text = "Insert",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AlphaKeyboardLayout(
    state: KeyboardUiState,
    onChar: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onAction: () -> Unit,
    onToggleShift: () -> Unit,
    onSwitchToNumbers: () -> Unit,
    onSwitchToEmoji: () -> Unit
) {
    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            row1.forEach { char ->
                val display = if (state.isShifted) char.uppercase() else char
                Keycap(
                    label = display,
                    modifier = Modifier.weight(1f),
                    onClick = { onChar(display) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            row2.forEach { char ->
                val display = if (state.isShifted) char.uppercase() else char
                Keycap(
                    label = display,
                    modifier = Modifier.weight(1f),
                    onClick = { onChar(display) }
                )
            }
        }

        // Row 3 (Shift, Z-M, Delete)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Shift Key
            SpecialKeycap(
                modifier = Modifier.weight(1.4f),
                onClick = onToggleShift,
                isActive = state.isShifted,
                activeColor = NeonCyan
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = "Shift",
                    tint = if (state.isShifted) NeonCyan else Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            row3.forEach { char ->
                val display = if (state.isShifted) char.uppercase() else char
                Keycap(
                    label = display,
                    modifier = Modifier.weight(1f),
                    onClick = { onChar(display) }
                )
            }

            // Backspace Key
            SpecialKeycap(
                modifier = Modifier.weight(1.4f),
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Row 4 (Switch, Emoji, Space, Period, Action)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ?123
            SpecialKeycap(
                modifier = Modifier.weight(1.3f),
                onClick = onSwitchToNumbers
            ) {
                Text(text = "?123", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            // Emoji
            SpecialKeycap(
                modifier = Modifier.weight(1.0f),
                onClick = onSwitchToEmoji
            ) {
                Text(text = "😊", fontSize = 18.sp)
            }

            // Spacebar
            Keycap(
                label = if (state.isSinglishEnabled) "සිංහල (Singlish)" else if (state.isSecretChatEnabled) "🔒 Secret Space" else "English",
                modifier = Modifier.weight(4.5f),
                fontSize = 12.sp,
                isAccented = state.isSinglishEnabled || state.isSecretChatEnabled,
                accentColor = if (state.isSecretChatEnabled) NeonAmber else if (state.isSinglishEnabled) NeonPurple else NeonCyan,
                onClick = onSpace
            )

            // Period
            Keycap(
                label = ".",
                modifier = Modifier.weight(1.0f),
                onClick = { onChar(".") }
            )

            // Enter / Send
            SpecialKeycap(
                modifier = Modifier.weight(1.5f),
                onClick = onAction,
                isActive = true,
                activeColor = NeonCyan
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enter",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun NumericSymbolKeyboardLayout(
    state: KeyboardUiState,
    isSecondarySymbols: Boolean,
    onChar: (String) -> Unit,
    onDelete: () -> Unit,
    onSpace: () -> Unit,
    onAction: () -> Unit,
    onSwitchToAlpha: () -> Unit,
    onSwitchToMoreSymbols: () -> Unit
) {
    val row1 = if (!isSecondarySymbols) {
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    } else {
        listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    }

    val row2 = if (!isSecondarySymbols) {
        listOf("@", "#", "$", "_", "&", "-", "+", "(", ")", "/")
    } else {
        listOf("£", "€", "¥", "¢", "©", "®", "™", "[", "]", "\\")
    }

    val row3 = if (!isSecondarySymbols) {
        listOf("*", "\"", "'", ":", ";", "!", "?")
    } else {
        listOf("<", ">", "=", "{", "}", "%", "^")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            row1.forEach { char ->
                Keycap(label = char, modifier = Modifier.weight(1f), onClick = { onChar(char) })
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            row2.forEach { char ->
                Keycap(label = char, modifier = Modifier.weight(1f), onClick = { onChar(char) })
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpecialKeycap(
                modifier = Modifier.weight(1.3f),
                onClick = onSwitchToMoreSymbols
            ) {
                Text(text = if (!isSecondarySymbols) "=/<" else "123", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            row3.forEach { char ->
                Keycap(label = char, modifier = Modifier.weight(1f), onClick = { onChar(char) })
            }

            SpecialKeycap(
                modifier = Modifier.weight(1.3f),
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpecialKeycap(
                modifier = Modifier.weight(1.5f),
                onClick = onSwitchToAlpha
            ) {
                Text(text = "ABC", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Keycap(label = ",", modifier = Modifier.weight(1.0f), onClick = { onChar(",") })

            Keycap(label = "Space", modifier = Modifier.weight(4.5f), onClick = onSpace)

            Keycap(label = ".", modifier = Modifier.weight(1.0f), onClick = { onChar(".") })

            SpecialKeycap(
                modifier = Modifier.weight(1.5f),
                onClick = onAction,
                isActive = true,
                activeColor = NeonCyan
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enter",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun EmojiKeyboardLayout(
    onEmojiSelected: (String) -> Unit,
    onSwitchToAlpha: () -> Unit,
    onDelete: () -> Unit
) {
    val emojis = listOf(
        "😂", "❤️", "🔥", "🙏", "🇱🇰", "👍", "😍", "✨",
        "😊", "🥺", "🎉", "💯", "👏", "🤣", "🥰", "💪",
        "👌", "😘", "😎", "🚀", "⚡", "🌟", "🔒", "📱"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            emojis.chunked(8).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    row.forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .clickable { onEmojiSelected(emoji) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 22.sp)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpecialKeycap(
                modifier = Modifier.width(80.dp),
                onClick = onSwitchToAlpha
            ) {
                Text(text = "ABC", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                text = "SamuZA Quick Emojis",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )

            SpecialKeycap(
                modifier = Modifier.width(80.dp),
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// iOS 18 Glassmorphic Keycap Component
@Composable
fun Keycap(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    fontSize: androidx.compose.ui.unit.TextUnit = 19.sp,
    isAccented: Boolean = false,
    accentColor: Color = NeonCyan
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val keyBackground = if (isPressed) {
        accentColor.copy(alpha = 0.35f)
    } else if (isAccented) {
        accentColor.copy(alpha = 0.15f)
    } else {
        Color(0x382B374E)
    }

    val keyBorder = if (isPressed || isAccented) {
        accentColor.copy(alpha = 0.7f)
    } else {
        Color.White.copy(alpha = 0.14f)
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(
                elevation = if (isPressed) 1.dp else 3.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = if (isAccented) accentColor else Color.Black
            )
            .clip(RoundedCornerShape(10.dp))
            .background(keyBackground)
            .border(BorderStroke(1.dp, keyBorder), RoundedCornerShape(10.dp))
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isPressed) accentColor else Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SpecialKeycap(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    activeColor: Color = NeonCyan,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val keyBackground = if (isActive) {
        activeColor
    } else if (isPressed) {
        Color(0x70374768)
    } else {
        Color(0x281F283B)
    }

    val keyBorder = if (isActive) {
        activeColor
    } else {
        Color.White.copy(alpha = 0.10f)
    }

    Box(
        modifier = modifier
            .height(44.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color.Black
            )
            .clip(RoundedCornerShape(10.dp))
            .background(keyBackground)
            .border(BorderStroke(1.dp, keyBorder), RoundedCornerShape(10.dp))
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Checks clipboard for incoming secret messages
 */
fun checkClipboardForSecrets(
    context: Context,
    onSecretDetected: (plain: String, cipher: String) -> Unit
) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
        val clip = clipboard?.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val text = clip.getItemAt(0).text?.toString()
            if (text != null && SecretMessageCipher.isSecretMessage(text)) {
                val decoded = SecretMessageCipher.decode(text)
                if (decoded != null) {
                    onSecretDetected(decoded, text)
                }
            }
        }
    } catch (e: Exception) {
        // ignore
    }
}
