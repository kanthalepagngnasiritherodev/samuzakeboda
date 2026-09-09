package com.example.privacy

import android.util.Base64
import java.nio.charset.StandardCharsets

/**
 * 100% Offline Secret Message Encoder & Decoder.
 * Allows users to send privacy-protected messages via WhatsApp, Messenger, Telegram, or SMS.
 * Anyone with SamuZA Keyboard can auto-decode it when copied or pasted!
 */
object SecretMessageCipher {

    private const val PREFIX = "🔒SZ::"
    private const val SUFFIX = "::🔒"

    // XOR obfuscation key to prevent casual base64 inspection
    private val OBFUSCATION_KEY = byteArrayOf(0x53, 0x61, 0x6D, 0x75, 0x5A, 0x41) // "SamuZA"

    /**
     * Checks whether a given string contains a SamuZA secret message signature
     */
    fun isSecretMessage(text: CharSequence?): Boolean {
        if (text == null) return false
        val s = text.toString().trim()
        return s.contains(PREFIX) && s.contains(SUFFIX)
    }

    /**
     * Encodes plain text into a protected SamuZA secret message
     */
    fun encode(plainText: String): String {
        if (plainText.isEmpty()) return ""
        val bytes = plainText.toByteArray(StandardCharsets.UTF_8)
        val xorBytes = ByteArray(bytes.size)
        for (i in bytes.indices) {
            xorBytes[i] = (bytes[i].toInt() xor OBFUSCATION_KEY[i % OBFUSCATION_KEY.size].toInt()).toByte()
        }
        val encodedPayload = Base64.encodeToString(xorBytes, Base64.NO_WRAP)
        return "$PREFIX$encodedPayload$SUFFIX"
    }

    /**
     * Decodes an encrypted SamuZA secret message back into human-readable text.
     * Returns null if decoding fails or text isn't a secret message.
     */
    fun decode(cipherText: String): String? {
        val trimmed = cipherText.trim()
        val startIndex = trimmed.indexOf(PREFIX)
        val endIndex = trimmed.indexOf(SUFFIX)

        if (startIndex == -1 || endIndex == -1 || endIndex <= startIndex) {
            return null
        }

        val payload = trimmed.substring(startIndex + PREFIX.length, endIndex)
        return try {
            val decodedBytes = Base64.decode(payload, Base64.NO_WRAP)
            val originalBytes = ByteArray(decodedBytes.size)
            for (i in decodedBytes.indices) {
                originalBytes[i] = (decodedBytes[i].toInt() xor OBFUSCATION_KEY[i % OBFUSCATION_KEY.size].toInt()).toByte()
            }
            String(originalBytes, StandardCharsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Symbol/Emoji steganography cipher for fun stealth chats
     */
    fun encodeToEmojiStealth(plainText: String): String {
        val emojis = listOf("✨", "🌙", "⚡", "🔮", "💎", "⭐", "🔥", "🍀")
        val binary = plainText.toByteArray(StandardCharsets.UTF_8).joinToString("") { byte ->
            String.format("%8s", Integer.toBinaryString(byte.toInt() and 0xFF)).replace(' ', '0')
        }
        val result = StringBuilder("🔐[")
        for (i in 0 until binary.length step 3) {
            val chunk = binary.substring(i, minOf(i + 3, binary.length)).padEnd(3, '0')
            val idx = chunk.toInt(2)
            result.append(emojis[idx])
        }
        result.append("]🔐")
        return result.toString()
    }
}
