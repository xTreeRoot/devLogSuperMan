package org.treeroot.devlog.theme


import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*


/* -------------------------------------------------------------------------- */
/*                                Light Theme                                 */
/* -------------------------------------------------------------------------- */

val LightColorScheme = lightColorScheme(

    /* === Core Brand Colors === */

    primary = Color(0xFF4F6FFF),
    onPrimary = Color.White,

    primaryContainer = Color(0xFFE0E4FF),
    onPrimaryContainer = Color(0xFF0E1A60),

    secondary = Color(0xFFFF6B6B),
    onSecondary = Color.White,

    secondaryContainer = Color(0xFFFFD7D7),
    onSecondaryContainer = Color(0xFF6B0F0F),

    tertiary = Color(0xFF4ECDC4),
    onTertiary = Color.White,

    tertiaryContainer = Color(0xFFCFF8F4),
    onTertiaryContainer = Color(0xFF0A3D37),

    /* === Background & Surface === */

    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1A1A1A),

    surface = Color.White,
    onSurface = Color(0xFF1A1A1A),

    surfaceVariant = Color(0xFFE9ECEF),
    onSurfaceVariant = Color(0xFF50565C),

    /* === Outline / Border === */

    outline = Color(0xFFADB5BD),
    outlineVariant = Color(0xFFDDE1E4),

    /* === Error === */

    error = Color(0xFFBA1A1A),
    onError = Color.White,

    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    /* === Other === */

    inverseSurface = Color(0xFF2E3135),
    inverseOnSurface = Color(0xFFF8F9FA),
    inversePrimary = Color(0xFFBEC6FF),

    scrim = Color(0x66000000)
)

/* -------------------------------------------------------------------------- */
/*                                 Dark Theme                                 */
/* -------------------------------------------------------------------------- */

val DarkColorScheme = darkColorScheme(

    /* === Core Brand Colors === */

    primary = Color(0xFF8DA2FF),
    onPrimary = Color(0xFF0F1320),

    primaryContainer = Color(0xFF3A4470),
    onPrimaryContainer = Color(0xFFE0E4FF),

    secondary = Color(0xFFFF8F8F),
    onSecondary = Color(0xFF3A1010),

    secondaryContainer = Color(0xFF5A2B2B),
    onSecondaryContainer = Color(0xFFFFDADA),

    tertiary = Color(0xFF61E4CF),
    onTertiary = Color(0xFF003730),

    tertiaryContainer = Color(0xFF005A50),
    onTertiaryContainer = Color(0xFF86F7E3),

    /* === Background & Surface === */

    background = Color(0xFF111418),
    onBackground = Color(0xFFE6E8EE),

    surface = Color(0xFF181B20),
    onSurface = Color(0xFFE1E3E8),

    surfaceVariant = Color(0xFF2C2F36),
    onSurfaceVariant = Color(0xFFBFC2CB),

    surfaceTint = Color(0xFF8DA2FF),

    /* === Outline & Border === */

    outline = Color(0xFF444A55),
    outlineVariant = Color(0xFF30343D),

    /* === Error === */

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),

    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    /* === Others === */

    inverseSurface = Color(0xFFE6E8EE),
    inverseOnSurface = Color(0xFF1A1C1F),
    inversePrimary = Color(0xFF4359D9),

    scrim = Color(0x66000000)
)

/* -------------------------------------------------------------------------- */
/*                                DevLog Theme                                */
/* -------------------------------------------------------------------------- */

