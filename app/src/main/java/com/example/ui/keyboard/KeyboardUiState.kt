package com.example.ui.keyboard

enum class KeyboardLayout {
    ALPHA,
    NUMBERS,
    SYMBOLS,
    EMOJI
}

enum class ActiveDrawer {
    NONE,
    WHATSAPP_STATUSES,
    SECRET_CHAT,
    CLIPBOARD,
    SHORTCUTS,
    FANCY_FONTS,
    HASHTAG_VAULT
}

data class KeyboardUiState(
    val layout: KeyboardLayout = KeyboardLayout.ALPHA,
    val isShifted: Boolean = false,
    val isCapsLock: Boolean = false,
    val isSecretChatEnabled: Boolean = false,
    val isSinglishEnabled: Boolean = false,
    val isFancyTextEnabled: Boolean = false,
    val selectedFancyStyleId: String = "bold",
    val activeSinglishBuffer: String = "",
    val activeSinhalaPreview: String = "",
    val activeDrawer: ActiveDrawer = ActiveDrawer.NONE,
    val secretDraft: String = "",
    val detectedSecretMessage: String? = null,
    val decodedClipboardText: String? = null,
    val composingBuffer: String = "",
    val feedbackMessage: String? = null
)
