package org.treeroot.devlog.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.treeroot.devlog.json.model.UiConfig

data class DynamicColors(
    // Text Colors
    val textColor: Color,
    val textVariantColor: Color,
    val textOnPrimary: Color,
    val textOnSecondary: Color,
    val textOnTertiary: Color,
    val textOnError: Color,
    val textOnBackground: Color,
    val textOnSurface: Color,
    val textOnSurfaceVariant: Color,
    val textOnInverseSurface: Color,

    // Primary Colors
    val primaryColor: Color,
    val onPrimaryColor: Color,
    val primaryContainerColor: Color,
    val onPrimaryContainerColor: Color,

    // Secondary Colors
    val secondaryColor: Color,
    val onSecondaryColor: Color,
    val secondaryContainerColor: Color,
    val onSecondaryContainerColor: Color,

    // Tertiary Colors
    val tertiaryColor: Color,
    val onTertiaryColor: Color,
    val tertiaryContainerColor: Color,
    val onTertiaryContainerColor: Color,

    // Background & Surface Colors
    val backgroundColor: Color,
    val onBackgroundColor: Color,
    val surfaceColor: Color,
    val onSurfaceColor: Color,
    val surfaceVariantColor: Color,
    val onSurfaceVariantColor: Color,
    val surfaceTintColor: Color,
    val surfaceBrightColor: Color,
    val surfaceDimColor: Color,

    // Error Colors
    val errorColor: Color,
    val onErrorColor: Color,
    val errorContainerColor: Color,
    val onErrorContainerColor: Color,

    // Outline Colors
    val outlineColor: Color,
    val outlineVariantColor: Color,

    // Inverse Colors
    val inverseSurfaceColor: Color,
    val inverseOnSurfaceColor: Color,
    val inversePrimaryColor: Color,

    // Scrim Color
    val scrimColor: Color
)

object ColorUtils {

    @Composable
    fun getDynamicColors(config: UiConfig?): DynamicColors {
        val cs = MaterialTheme.colorScheme
        val hasBg = config?.backgroundImagePath?.isNotEmpty() == true
        fun c(light: Color, dark: Color) =
            if (hasBg) light else dark
        return DynamicColors(

            // Text
            textColor = c(Color.White, cs.onSurface),
            textVariantColor = c(Color.LightGray, cs.onSurfaceVariant),
            textOnPrimary = c(Color.Black, cs.onPrimary),
            textOnSecondary = c(Color.Black, cs.onSecondary),
            textOnTertiary = c(Color.Black, cs.onTertiary),
            textOnError = c(Color.Black, cs.onError),
            textOnBackground = c(Color.White, cs.onBackground),
            textOnSurface = c(Color.White, cs.onSurface),
            textOnSurfaceVariant = c(Color.LightGray, cs.onSurfaceVariant),
            textOnInverseSurface = c(Color.LightGray, cs.inverseOnSurface),

            // Primary
            primaryColor = c(Color.White, cs.primary),
            onPrimaryColor = c(Color.Black, cs.onPrimary),
            primaryContainerColor = c(Color(0xFFE0E0E0), cs.primaryContainer),
            onPrimaryContainerColor = c(Color.DarkGray, cs.onPrimaryContainer),

            // Secondary
            secondaryColor = c(Color(0xFFCCCCCC), cs.secondary),
            onSecondaryColor = c(Color.Black, cs.onSecondary),
            secondaryContainerColor = c(Color(0xFFD0D0D0), cs.secondaryContainer),
            onSecondaryContainerColor = c(Color.DarkGray, cs.onSecondaryContainer),

            // Tertiary
            tertiaryColor = c(Color(0xFFBBBBBB), cs.tertiary),
            onTertiaryColor = c(Color.Black, cs.onTertiary),
            tertiaryContainerColor = c(Color(0xFFC0C0C0), cs.tertiaryContainer),
            onTertiaryContainerColor = c(Color.DarkGray, cs.onTertiaryContainer),

            // Surface / Background
            backgroundColor = c(Color.Black.copy(alpha = 0.3f), cs.background),
            onBackgroundColor = c(Color.White, cs.onBackground),
            surfaceColor = c(Color.Transparent, cs.surface),
            onSurfaceColor = c(Color.White, cs.onSurface),
            surfaceVariantColor = c(Color(0xFF555555), cs.surfaceVariant),
            onSurfaceVariantColor = c(Color.LightGray, cs.onSurfaceVariant),
            surfaceTintColor = c(Color.White, cs.surfaceTint),
            surfaceBrightColor = c(Color(0xFF333333), cs.surfaceBright),
            surfaceDimColor = c(Color(0xFF222222), cs.surfaceDim),

            // Error
            errorColor = c(Color(0xFFFFB3AF), cs.error),
            onErrorColor = c(Color.DarkGray, cs.onError),
            errorContainerColor = c(Color(0xFF93000A), cs.errorContainer),
            onErrorContainerColor = c(Color(0xFFFFDAD6), cs.onErrorContainer),

            // Outline
            outlineColor = c(Color(0xFF888888), cs.outline),
            outlineVariantColor = c(Color(0xFF666666), cs.outlineVariant),

            // Inverse
            inverseSurfaceColor = c(Color(0xFFE0E0E0), cs.inverseSurface),
            inverseOnSurfaceColor = c(Color.DarkGray, cs.inverseOnSurface),
            inversePrimaryColor = c(Color(0xFF3366FF), cs.inversePrimary),

            // Scrim
            scrimColor = c(Color.Black.copy(alpha = 0.4f), cs.scrim)
        )
    }

    /**
     * 根据配置决定容器背景色
     * 如果有背景图片则透明，否则使用 onSurface 颜色
     */
    @Composable
    fun getContainerBackgroundColor(config: UiConfig?): Color {
        return if (config?.backgroundImagePath?.isNotEmpty() == true) {
            Color.Transparent
        } else {
            Color.White
        }
    }
}