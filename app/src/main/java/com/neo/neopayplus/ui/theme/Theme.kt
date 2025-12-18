package com.neo.neopayplus.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = IndigoBlue,
    onPrimary = White,
    background = Background,
    onBackground = IndigoBlue, // Use IndigoBlue for text on dark background
    surface = Surface,
    onSurface = MutedLavender, // Use MutedLavender for secondary text
    secondary = MutedLavender,
    error = ErrorRed
)

@Composable
fun NeoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = NeoTypography,
        shapes = Shapes(
            extraSmall = MaterialTheme.shapes.extraSmall,
            small = MaterialTheme.shapes.small,
            medium = MaterialTheme.shapes.medium,
            large = MaterialTheme.shapes.large,
            extraLarge = MaterialTheme.shapes.extraLarge
        ),
        content = content
    )
}
