package com.example.util

/**
 * Standard Wijesekara Sinhala Keyboard Layout mapping.
 * Direct key mappings for normal and shifted states.
 */
object WijesekaraLayout {
    // Normal Wijesekara mapping (Row 1-4)
    val normalRow1 = listOf(
        "q" to "ු", "w" to "අ", "e" to "ැ", "r" to "ර", "t" to "එ", "y" to "හ", "u" to "ම", "i" to "ස", "o" to "ද", "p" to "ච"
    )
    val normalRow2 = listOf(
        "a" to "්", "s" to "ි", "d" to "ා", "f" to "ෙ", "g" to "ට", "h" to "ය", "j" to "ව", "k" to "න", "l" to "ක"
    )
    val normalRow3 = listOf(
        "z" to "ඤ", "x" to "ං", "c" to "ජ", "v" to "ඩ", "b" to "ඉ", "n" to "බ", "m" to "ප"
    )

    // Shifted Wijesekara mapping
    val shiftedRow1 = listOf(
        "q" to "ූ", "w" to "උ", "e" to "ෑ", "r" to "ඍ", "t" to "ඓ", "y" to "ශ", "u" to "ඹ", "i" to "ෂ", "o" to "ධ", "p" to "ඡ"
    )
    val shiftedRow2 = listOf(
        "a" to "ෟ", "s" to "ී", "d" to "ෘ", "f" to "ෆ", "g" to "ඨ", "h" to "්‍ය", "j" to "ළු", "k" to "ණ", "l" to "ඛ"
    )
    val shiftedRow3 = listOf(
        "z" to "ඥ", "x" to "ඃ", "c" to "ඣ", "v" to "ඪ", "b" to "ඊ", "n" to "භ", "m" to "ඵ"
    )
}
