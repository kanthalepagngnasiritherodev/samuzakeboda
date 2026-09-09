package com.example.util

data class FancyStyle(
    val id: String,
    val name: String,
    val sample: String,
    val converter: (String) -> String
)

/**
 * 100% Offline Fancy Text Style Generator.
 * Converts standard Latin text into Mathematical Bold, Script, Monospace,
 * Double-Struck, Gothic, Underlined, and other Unicode styles for social media captions.
 */
object FancyTextGenerator {

    private fun mapChars(text: String, mapUpper: (Char) -> String, mapLower: (Char) -> String, mapDigit: ((Char) -> String)? = null): String {
        val sb = StringBuilder()
        for (ch in text) {
            when {
                ch in 'A'..'Z' -> sb.append(mapUpper(ch))
                ch in 'a'..'z' -> sb.append(mapLower(ch))
                mapDigit != null && ch in '0'..'9' -> sb.append(mapDigit(ch))
                else -> sb.append(ch)
            }
        }
        return sb.toString()
    }

    // 1. Mathematical Sans-Serif Bold (𝗕𝗼𝗹𝗱)
    fun toBold(text: String): String = mapChars(
        text,
        mapUpper = { String(Character.toChars(0x1D5D4 + (it - 'A'))) },
        mapLower = { String(Character.toChars(0x1D5EE + (it - 'a'))) },
        mapDigit = { String(Character.toChars(0x1D7EC + (it - '0'))) }
    )

    // 2. Mathematical Bold Italic (𝑩𝒐𝒍𝒅 𝑰𝒕𝒂𝒍𝒊𝒄)
    fun toBoldItalic(text: String): String = mapChars(
        text,
        mapUpper = { String(Character.toChars(0x1D468 + (it - 'A'))) },
        mapLower = { String(Character.toChars(0x1D482 + (it - 'a'))) }
    )

    // 3. Script / Cursive (𝓢𝓬𝓻𝓲𝓹𝓽)
    fun toScript(text: String): String = mapChars(
        text,
        mapUpper = { String(Character.toChars(0x1D4D0 + (it - 'A'))) },
        mapLower = { String(Character.toChars(0x1D4EA + (it - 'a'))) }
    )

    // 4. Double-Struck (Blackboard Bold: 𝔻𝕠𝕦𝕓𝕝𝕖)
    fun toDoubleStruck(text: String): String {
        return mapChars(
            text,
            mapUpper = { ch ->
                when (ch) {
                    'C' -> "ℂ"
                    'H' -> "ℍ"
                    'N' -> "ℕ"
                    'P' -> "ℙ"
                    'Q' -> "ℚ"
                    'R' -> "ℝ"
                    'Z' -> "ℤ"
                    else -> String(Character.toChars(0x1D538 + (ch - 'A')))
                }
            },
            mapLower = { ch -> String(Character.toChars(0x1D552 + (ch - 'a'))) },
            mapDigit = { ch -> String(Character.toChars(0x1D7D8 + (ch - '0'))) }
        )
    }

    // 5. Monospace / Typewriter (𝙼𝚘𝚗𝚘)
    fun toMonospace(text: String): String = mapChars(
        text,
        mapUpper = { String(Character.toChars(0x1D670 + (it - 'A'))) },
        mapLower = { String(Character.toChars(0x1D68A + (it - 'a'))) },
        mapDigit = { String(Character.toChars(0x1D7F6 + (it - '0'))) }
    )

    // 6. Fraktur / Gothic (𝔉𝔯𝔞𝔨𝔱𝔲𝔯)
    fun toFraktur(text: String): String {
        return mapChars(
            text,
            mapUpper = { ch ->
                when (ch) {
                    'C' -> "ℭ"
                    'H' -> "ℌ"
                    'I' -> "ℑ"
                    'R' -> "ℜ"
                    'Z' -> "ℨ"
                    else -> String(Character.toChars(0x1D504 + (ch - 'A')))
                }
            },
            mapLower = { ch -> String(Character.toChars(0x1D51E + (ch - 'a'))) }
        )
    }

    // 7. Underlined Style (U̲n̲d̲e̲r̲l̲i̲n̲e̲d̲)
    fun toUnderlined(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(ch)
            if (ch != ' ') {
                sb.append('\u0332') // Combining low line
            }
        }
        return sb.toString()
    }

    // 8. Strikethrough Style (S̶t̶r̶i̶k̶e̶)
    fun toStrikethrough(text: String): String {
        val sb = StringBuilder()
        for (ch in text) {
            sb.append(ch)
            if (ch != ' ') {
                sb.append('\u0336') // Combining long stroke overlay
            }
        }
        return sb.toString()
    }

    // 9. Circled / Bubble (Ⓒⓘⓡⓒⓛⓔⓓ)
    fun toCircled(text: String): String = mapChars(
        text,
        mapUpper = { String(Character.toChars(0x24B6 + (it - 'A'))) },
        mapLower = { String(Character.toChars(0x24D0 + (it - 'a'))) },
        mapDigit = { ch ->
            if (ch == '0') "⓪"
            else String(Character.toChars(0x2460 + (ch - '1')))
        }
    )

    val styles = listOf(
        FancyStyle("bold", "Math Bold", "𝗕𝗼𝗹𝗱 𝗖𝗮𝗽𝘁𝗶𝗼𝗻", ::toBold),
        FancyStyle("bold_italic", "Bold Italic", "𝑩𝒐𝒍𝒅 𝑰𝒕𝒂𝒍𝒊𝒄", ::toBoldItalic),
        FancyStyle("script", "Script / Cursive", "𝓢𝓬𝓻𝓲𝓹𝓽 𝓒𝓾𝓻𝓼𝓲𝓿𝓮", ::toScript),
        FancyStyle("double", "Double-Struck", "𝔻𝕠𝕦𝕓𝕝𝕖 𝕊𝕥𝕣𝕦𝕔𝕜", ::toDoubleStruck),
        FancyStyle("mono", "Monospace", "𝙼𝚘𝚗𝚘𝚜𝚙𝚊𝚌𝚎", ::toMonospace),
        FancyStyle("fraktur", "Gothic Fraktur", "𝔉𝔯𝔞𝔨𝔱𝔲𝔯 𝔖𝔱𝔶𝔩𝔢", ::toFraktur),
        FancyStyle("underlined", "Underlined", "U̲n̲d̲e̲r̲l̲i̲n̲e̲d̲", ::toUnderlined),
        FancyStyle("strike", "Strikethrough", "S̶t̶r̶i̶k̶e̶t̶h̶r̶o̶u̶g̶h̶", ::toStrikethrough),
        FancyStyle("circled", "Bubble Circled", "Ⓑⓤⓑⓑⓛⓔ", ::toCircled)
    )

    fun convert(text: String, styleId: String): String {
        val style = styles.find { it.id == styleId } ?: return text
        return style.converter(text)
    }
}
