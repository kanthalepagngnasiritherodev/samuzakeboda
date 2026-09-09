package com.example.util

/**
 * 100% Offline Singlish (English phonetic to Sinhala Unicode) conversion engine.
 * Fast, lightweight, and operates entirely locally without any network dependencies.
 */
object SinglishConverter {

    // Vowel sound mappings (standalone at start of word or after another vowel)
    private val standaloneVowels = mapOf(
        "aae" to "ඈ",
        "aaw" to "ඈ",
        "aai" to "ඓ",
        "aa" to "ආ",
        "a" to "අ",
        "ii" to "ඊ",
        "ee" to "ඊ",
        "i" to "ඉ",
        "uu" to "ඌ",
        "u" to "උ",
        "ei" to "ඒ",
        "E" to "ඒ",
        "e" to "එ",
        "oo" to "ඕ",
        "O" to "ඕ",
        "o" to "ඔ",
        "au" to "ඖ",
        "ai" to "ඓ",
        "ae" to "ඇ"
    )

    // Pillam (vowel modifiers attached to consonants)
    // Explicitly supporting user rules:
    // 'a' -> removes hal-kirima ("")
    // 'aa' -> "ා"
    // 'i' -> "ි"
    // 'ee' -> "ී"
    // 'u' -> "හු" ("ු")
    // 'e' -> "ෙ"
    // 'o' -> "ො"
    private val vowelModifiers = mapOf(
        "aae" to "ෑ",
        "aaw" to "ෑ",
        "aa" to "ා",
        "a" to "", // inherent vowel: removes Hal Kirima
        "ii" to "ී",
        "ee" to "ී", // user rule: 'h' + 'ee' -> 'හී'
        "i" to "ි",
        "uu" to "ූ",
        "u" to "ු",
        "ei" to "ේ",
        "E" to "ේ",
        "e" to "ෙ",
        "oo" to "ෝ",
        "O" to "ෝ",
        "o" to "ො",
        "au" to "ෞ",
        "ai" to "ෛ",
        "ae" to "ැ"
    )

    // Multi-char and single-char consonant mappings
    private val consonants = listOf(
        "ndh" to "ඳ",
        "nd" to "ඬ",
        "mb" to "ඹ",
        "ng" to "ඟ",
        "ny" to "ඥ",
        "ch" to "ච",
        "th" to "ත",
        "dh" to "ද",
        "sh" to "ශ",
        "Sh" to "ෂ",
        "kh" to "ඛ",
        "gh" to "ඝ",
        "jh" to "ඣ",
        "ph" to "ඵ",
        "bh" to "භ",
        "Th" to "ඨ",
        "Dh" to "ඪ",
        "k" to "ක",
        "g" to "ග",
        "j" to "ජ",
        "t" to "ට",
        "d" to "ඩ",
        "n" to "න",
        "N" to "ණ",
        "p" to "ප",
        "b" to "බ",
        "m" to "ම",
        "y" to "ය",
        "r" to "ර",
        "l" to "ල",
        "L" to "ළ" ,
        "w" to "ව",
        "v" to "ව",
        "s" to "ස",
        "h" to "හ",
        "f" to "ෆ"
    )

    // Common words lookup for instant natural Singlish conversion
    private val quickWords = mapOf(
        "mama" to "මම",
        "oya" to "ඔයා",
        "oyaa" to "ඔයා",
        "api" to "අපි",
        "eyaa" to "එයා",
        "eya" to "එයා",
        "monawada" to "මොනවද",
        "mokakda" to "මොකක්ද",
        "mokadda" to "මොකක්ද",
        "kohomada" to "කොහොමද",
        "kohomadha" to "කොහොමද",
        "suba" to "සුබ",
        "dawasak" to "දවසක්",
        "udasanak" to "උදෑසනක්",
        "udhasanak" to "උදෑසනක්",
        "rathriyak" to "රාත්‍රියක්",
        "karanna" to "කරන්න",
        "enawa" to "එනවා",
        "yanawa" to "යනවා",
        "innawa" to "ඉන්නවා",
        "kanawa" to "කනවා",
        "bonawa" to "බොනවා",
        "gedara" to "ගෙදර",
        "gamana" to "ගමන",
        "pothak" to "පොතක්",
        "wada" to "වැඩ",
        "hithanna" to "හිතන්න",
        "lassana" to "ලස්සන",
        "sthuthiyi" to "ස්තූතියි",
        "bohoma" to "බොහොම",
        "sinhala" to "සිංහල",
        "sri" to "ශ්‍රී",
        "lanka" to "ලංකා",
        "samuza" to "සමුසා"
    )

    /**
     * Converts an entire sentence or phrase from Singlish to Sinhala.
     */
    fun convert(input: String): String {
        if (input.isEmpty()) return ""

        val words = input.split(" ")
        return words.joinToString(" ") { word ->
            convertWord(word)
        }
    }

    /**
     * Converts a single word from Singlish to Sinhala.
     */
    fun convertWord(rawWord: String): String {
        if (rawWord.isEmpty()) return ""

        // Preserve surrounding punctuation
        val prefix = rawWord.takeWhile { !it.isLetter() }
        val suffix = rawWord.takeLastWhile { !it.isLetter() }
        val core = rawWord.substring(prefix.length, rawWord.length - suffix.length)

        if (core.isEmpty()) return rawWord

        val lowerCore = core.lowercase()
        if (quickWords.containsKey(lowerCore)) {
            return prefix + quickWords[lowerCore] + suffix
        }

        val result = StringBuilder()
        var i = 0
        val len = core.length

        while (i < len) {
            // Check if there is a consonant match
            var matchedConsonant: String? = null
            var sinhalaConsonant: String? = null

            for ((cStr, sChar) in consonants) {
                if (core.startsWith(cStr, i, ignoreCase = (cStr != "N" && cStr != "L" && cStr != "Sh" && cStr != "Th" && cStr != "Dh"))) {
                    matchedConsonant = cStr
                    sinhalaConsonant = sChar
                    break
                }
            }

            if (sinhalaConsonant != null && matchedConsonant != null) {
                i += matchedConsonant.length

                // Check for subsequent vowel modifier
                var matchedVowel: String? = null
                var vowelPillam: String? = null

                // Sort keys by length descending to match 'aae', 'aa', 'a', etc.
                val sortedVowels = vowelModifiers.keys.sortedByDescending { it.length }
                for (vStr in sortedVowels) {
                    if (core.startsWith(vStr, i, ignoreCase = true)) {
                        matchedVowel = vStr
                        vowelPillam = vowelModifiers[vStr]
                        break
                    }
                }

                if (vowelPillam != null && matchedVowel != null) {
                    result.append(sinhalaConsonant)
                    result.append(vowelPillam)
                    i += matchedVowel.length
                } else {
                    // No vowel follows: append Virama (හල්කිරීම)
                    result.append(sinhalaConsonant)
                    result.append("්")
                }
            } else {
                // Check for standalone vowel
                var matchedVowel: String? = null
                var standaloneChar: String? = null

                val sortedStandalone = standaloneVowels.keys.sortedByDescending { it.length }
                for (vStr in sortedStandalone) {
                    if (core.startsWith(vStr, i, ignoreCase = true)) {
                        matchedVowel = vStr
                        standaloneChar = standaloneVowels[vStr]
                        break
                    }
                }

                if (standaloneChar != null && matchedVowel != null) {
                    result.append(standaloneChar)
                    i += matchedVowel.length
                } else {
                    // Unsupported character, keep as is
                    result.append(core[i])
                    i++
                }
            }
        }

        return prefix + result.toString() + suffix
    }
}
