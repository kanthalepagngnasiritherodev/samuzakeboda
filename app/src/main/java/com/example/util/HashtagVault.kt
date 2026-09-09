package com.example.util

data class HashtagCategory(
    val title: String,
    val iconEmoji: String,
    val hashtags: List<String>
)

object HashtagVault {

    val categories: List<HashtagCategory> = listOf(
        HashtagCategory(
            title = "Trending & Viral",
            iconEmoji = "🔥",
            hashtags = listOf(
                "#viral", "#trending", "#fyp", "#explorepage", "#foryou",
                "#viralreels", "#trendingnow", "#reelsinstagram", "#viralpost", "#explore"
            )
        ),
        HashtagCategory(
            title = "Sri Lanka Creators",
            iconEmoji = "🇱🇰",
            hashtags = listOf(
                "#srilanka", "#colombo", "#srilankadaily", "#exploresrilanka",
                "#ceylon", "#lka", "#srilankatrip", "#srilankaviews", "#visitsrilanka"
            )
        ),
        HashtagCategory(
            title = "Business & Orders",
            iconEmoji = "🛍️",
            hashtags = listOf(
                "#onlinebusiness", "#cashondelivery", "#islandwidedelivery", "#smallbusiness",
                "#shoplocal", "#srilankashopping", "#ordernow", "#discount", "#sale"
            )
        ),
        HashtagCategory(
            title = "Fashion & Style",
            iconEmoji = "👗",
            hashtags = listOf(
                "#fashion", "#style", "#ootd", "#aesthetic",
                "#menswear", "#womensfashion", "#trendingfashion", "#lifestyle"
            )
        ),
        HashtagCategory(
            title = "Food & Cafe",
            iconEmoji = "🍔",
            hashtags = listOf(
                "#foodie", "#srilankanfood", "#colombofood", "#instafood",
                "#delicious", "#cafecolombo", "#foodlover", "#streetfood"
            )
        ),
        HashtagCategory(
            title = "Tech & Creators",
            iconEmoji = "🎬",
            hashtags = listOf(
                "#contentcreator", "#photography", "#videography", "#reels",
                "#filmmaking", "#digitalcreator", "#creator", "#graphicdesign"
            )
        )
    )

    val creatorEmojis: List<String> = listOf(
        "🔥", "🚀", "💎", "✨", "💯", "👑", "🏆",
        "🛍️", "📦", "💳", "🏷️", "📍", "📞", "💬",
        "📢", "⚡", "⭐", "🎯", "🎁", "🇱🇰", "☕"
    )
}
