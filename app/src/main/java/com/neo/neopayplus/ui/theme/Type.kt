package com.neo.neopayplus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val NeoTypography = Typography(
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = com.neo.neopayplus.ui.theme.White),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = com.neo.neopayplus.ui.theme.MutedLavender),
    bodyMedium = TextStyle(fontSize = 14.sp, color = com.neo.neopayplus.ui.theme.White),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = com.neo.neopayplus.ui.theme.White)
)
