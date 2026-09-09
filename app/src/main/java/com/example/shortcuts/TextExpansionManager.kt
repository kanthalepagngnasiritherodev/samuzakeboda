package com.example.shortcuts

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class TextShortcut(
    val id: String,
    val trigger: String,
    val expandedText: String,
    val category: String = "General"
)

object TextExpansionManager {

    private const val PREFS_NAME = "samuza_shortcuts_prefs"
    private const val KEY_SHORTCUTS = "shortcuts_list"

    fun getShortcuts(context: Context): List<TextShortcut> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_SHORTCUTS, null)

        if (jsonStr.isNullOrEmpty()) {
            val defaults = getDefaultShortcuts()
            saveShortcuts(context, defaults)
            return defaults
        }

        val list = mutableListOf<TextShortcut>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    TextShortcut(
                        id = obj.optString("id", System.currentTimeMillis().toString()),
                        trigger = obj.getString("trigger"),
                        expandedText = obj.getString("expandedText"),
                        category = obj.optString("category", "General")
                    )
                )
            }
        } catch (e: Exception) {
            return getDefaultShortcuts()
        }
        return list
    }

    fun saveShortcuts(context: Context, shortcuts: List<TextShortcut>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val arr = JSONArray()
        for (s in shortcuts) {
            val obj = JSONObject().apply {
                put("id", s.id)
                put("trigger", s.trigger)
                put("expandedText", s.expandedText)
                put("category", s.category)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_SHORTCUTS, arr.toString()).apply()
    }

    fun addShortcut(context: Context, trigger: String, expandedText: String, category: String = "General") {
        val current = getShortcuts(context).toMutableList()
        current.removeAll { it.trigger.equals(trigger.trim(), ignoreCase = true) }
        current.add(
            0,
            TextShortcut(
                id = System.currentTimeMillis().toString(),
                trigger = trigger.trim(),
                expandedText = expandedText.trim(),
                category = category
            )
        )
        saveShortcuts(context, current)
    }

    fun removeShortcut(context: Context, id: String) {
        val current = getShortcuts(context).toMutableList()
        current.removeAll { it.id == id }
        saveShortcuts(context, current)
    }

    /**
     * Finds expansion for a typed trigger token (e.g. "#bank", "bank", "#cod", "addr")
     */
    fun findExpansion(context: Context, word: String): String? {
        val clean = word.trim().lowercase()
        val cleanWithoutHash = if (clean.startsWith("#") || clean.startsWith("!")) clean.substring(1) else clean
        val list = getShortcuts(context)
        return list.firstOrNull {
            val t = it.trigger.lowercase()
            val tWithoutHash = if (t.startsWith("#") || t.startsWith("!")) t.substring(1) else t
            t == clean || tWithoutHash == cleanWithoutHash || tWithoutHash == clean
        }?.expandedText
    }

    private fun getDefaultShortcuts(): List<TextShortcut> {
        return listOf(
            TextShortcut(
                id = "sc_bank",
                trigger = "#bank",
                expandedText = "Bank Account Details:\nBank: Bank of Ceylon (BOC)\nAccount Name: Page Admin Official\nAccount No: 7891234567\nBranch: City Center\n(Please send payment slip once deposited)",
                category = "Banking & Finance"
            ),
            TextShortcut(
                id = "sc_cod",
                trigger = "#cod",
                expandedText = "Cash on Delivery (COD) Order Form:\n• Full Name:\n• Delivery Address:\n• Nearest City:\n• Phone Number 1:\n• Phone Number 2:\n• Item & Quantity:",
                category = "Orders & E-Commerce"
            ),
            TextShortcut(
                id = "sc_price",
                trigger = "#price",
                expandedText = "Pricing & Packages:\n• Single Item: LKR 2,450\n• Combo Bundle (2x): LKR 4,500 [Free Delivery]\n• Islandwide COD available (Delivery within 48-72 hrs)",
                category = "Price Lists"
            ),
            TextShortcut(
                id = "sc_reply",
                trigger = "#reply",
                expandedText = "Hello! Thank you for reaching out to us. We have received your inquiry and our team will get back to you shortly with full details!",
                category = "Quick Replies"
            ),
            TextShortcut(
                id = "sc_social",
                trigger = "#links",
                expandedText = "Follow our official channels:\nInstagram: @SamuZaPage\nTikTok: @SamuZaCreator\nWhatsApp Hotline: +94 77 123 4567\nWebsite: www.samuza.lk",
                category = "Social Media"
            ),
            TextShortcut(
                id = "sc_addr",
                trigger = "#addr",
                expandedText = "No. 124, Lotus Grove, Colombo 03, Sri Lanka",
                category = "Address"
            ),
            TextShortcut(
                id = "sc_tel",
                trigger = "#tel",
                expandedText = "+94 77 123 4567",
                category = "Contact"
            )
        )
    }
}
