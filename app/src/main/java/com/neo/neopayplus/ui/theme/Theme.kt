package com.neo.neopayplus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.neo.neopayplus.ui.theme.NeoColors

// Dark Fintech Color Scheme
private val DarkFintechColorScheme = darkColorScheme(
    primary = NeoColors.AccentLavender,
    onPrimary = NeoColors.TextPrimary,
    secondary = NeoColors.MutedLavender,
    onSecondary = NeoColors.TextPrimary,
    surface = NeoColors.Surface,
    onSurface = NeoColors.TextPrimary,
    background = NeoColors.Background,
    onBackground = NeoColors.TextPrimary,
    error = NeoColors.Void,
    onError = NeoColors.TextPrimary
)

@Composable
fun NeoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkFintechColorScheme,
        typography = Typography,
        content = content
    )
}
