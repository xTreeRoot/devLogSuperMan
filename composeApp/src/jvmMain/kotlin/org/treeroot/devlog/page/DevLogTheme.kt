package org.treeroot.devlog.page

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.treeroot.devlog.theme.DarkColorScheme
import org.treeroot.devlog.theme.LightColorScheme

@Composable
fun DevLogTheme(
    darkTheme: Boolean = calculateDarkTheme(), // 根据配置计算是否使用暗色主题
    content: @Composable () -> Unit
) {
    val colorScheme =
        if (darkTheme) DarkColorScheme else LightColorScheme

    val typography = Typography(
        headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
        headlineMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium),
        titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium),

        bodyLarge = TextStyle(fontSize = 16.sp),
        bodyMedium = TextStyle(fontSize = 14.sp),
        labelSmall = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
    )

    val shapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(6.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(12.dp),
        extraLarge = RoundedCornerShape(28.dp)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

/**
 * 根据UiConfig配置计算是否使用暗色主题
 */
@Composable
private fun calculateDarkTheme(): Boolean {
    val config = org.treeroot.devlog.state.AppStateManager.currentConfig

    return if (config != null) {
        if (config.isSystemAdaptive) {
            isSystemInDarkTheme()
        } else {
            config.isDarkTheme
        }
    } else {
        isSystemInDarkTheme()
    }
}