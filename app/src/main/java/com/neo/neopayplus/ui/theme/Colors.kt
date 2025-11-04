package com.neo.neopayplus.ui.theme

import androidx.compose.ui.graphics.Color

// Dark Fintech Color Palette
// Apple + Stripe + SumUp aesthetic for Sunmi POS

object NeoColors {
    // Background colors
    val Background = Color(0xFF000000)           // Pure black
    val Surface = Color(0xFF121212)             // Dark card surface
    val CardSurface = Color(0xFF1E1E1E)         // Button/card background
    
    // Text colors
    val TextPrimary = Color(0xFFFFFFFF)          // White for primary text
    val TextSecondary = Color(0xFFB3B5FF)       // Lavender for secondary text
    
    // Accent colors (lavender theme)
    val MutedLavender = Color(0xFFB3B5FF)        // Lavender for icons/secondary
    val MutedLavender30 = Color(0x30B3B5FF)      // 30% opacity for borders
    val AccentLavender = Color(0xFF6666FF)       // Brighter lavender for focus/highlights
    val AccentLavender30 = Color(0x306666FF)     // 30% opacity for shadows
    
    // Action colors (keep original for differentiation)
    val Payment = Color(0xFF2A9D8F)             // Teal for payment
    val Refund = Color(0xFF3A86FF)               // Blue for refund
    val Void = Color(0xFFEF476F)                 // Red for void
    val Settlement = Color(0xFF8338EC)          // Purple for settlement
    val History = Color(0xFFFB8500)              // Orange for history
}

