package com.example.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import com.example.privacy.SecretMessageCipher
import org.json.JSONArray

object ClipboardHistoryManager {

    private const val PREFS_NAME = "samuza_clipboard_prefs"
    private const val KEY_HISTORY = "clipboard_items"
    private const val MAX_ITEMS = 20

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            // Seed initial helpful items if empty
            if (getItems(context).isEmpty()) {
                val initialItems = listOf(
                    "SamuZA Smart Utility Keyboard - 100% Offline",
                    "Welcome to your smart clipboard! Tap any clip to paste.",
                    "🔒SZ::S1dJUkZ3MEZRVUE9::🔒" // Demo secret message ("Hello World")
                )
                for (item in initialItems.reversed()) {
                    addItem(context, item)
                }
            }
        }
    }

    fun getItems(context: Context): List<String> {
        val p = prefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = p.getString(KEY_HISTORY, "[]") ?: "[]"
        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                list.add(arr.getString(i))
            }
        } catch (e: Exception) {
            // fallback
        }
        return list
    }

    fun addItem(context: Context, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val p = prefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getItems(context).toMutableList()
        current.remove(trimmed)
        current.add(0, trimmed)

        while (current.size > MAX_ITEMS) {
            current.removeAt(current.size - 1)
        }

        saveList(p, current)
    }

    fun removeItem(context: Context, text: String) {
        val p = prefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val current = getItems(context).toMutableList()
        current.remove(text)
        saveList(p, current)
    }

    fun clearAll(context: Context) {
        val p = prefs ?: context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        saveList(p, emptyList())
    }

    fun copyToSystemClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("SamuZA Copied Text", text)
        clipboard?.setPrimaryClip(clip)
        addItem(context, text)
    }

    private fun saveList(p: SharedPreferences, list: List<String>) {
        val arr = JSONArray()
        for (item in list) {
            arr.put(item)
        }
        p.edit().putString(KEY_HISTORY, arr.toString()).apply()
    }
}
