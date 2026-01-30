package org.treeroot.devlog.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

// 治愈系色彩定义
val LightColorScheme = lightColorScheme(
    // 柔和蓝色
    primary = Color(0xFF4F6FFF),
    onPrimary = Color.White,
    // 温暖珊瑚红
    secondary = Color(0xFFFF6B6B),
    onSecondary = Color.White,
    // 柔和青绿色
    tertiary = Color(0xFF4ECDC4),
    onTertiary = Color.White,
    // 雾白色背景
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1A1A1A),
    // 纯白表面
    surface = Color(0xFFFFFFFF),
    // 纯黑背景
    onSurface = Color(0xFF1A1A1A),
    // 淡灰色变体
    surfaceVariant = Color(0xFFE9ECEF),
    // 淡边框色
)

val DarkColorScheme = darkColorScheme(
    // 柔和蓝色
    primary = Color(0xFF6C8BFF),
    onPrimary = Color.White,
    // 温暖珊瑚红
    secondary = Color(0xFFFF7F7F),
    onSecondary = Color.White,
    // 柔和青绿色
    tertiary = Color(0xFF60DFD8),
    onTertiary = Color.White,
    // 深色背景
    background = Color(0xFF121212),
    // 深色表面
    onBackground = Color(0xFFE6E6E6),
    // 深色表面
    surface = Color(0xFF1E1E1E),
    // 深色背景
    onSurface = Color(0xFFE6E6E6),
    // 深灰色变体
    surfaceVariant = Color(0xFF2D2D2D),
    // 深边框色
    outline = Color(0xFF4A4A4A)
)

@Composable
fun DevLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            headlineLarge = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            headlineMedium = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            ),
            titleLarge = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            ),
            bodyLarge = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        ),
        content = content
    )
}