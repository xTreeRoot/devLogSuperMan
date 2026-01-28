package org.treeroot.devlog.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.treeroot.devlog.model.UiConfig

data class DynamicColors(
    val textColor: Color,
    val textVariantColor: Color,
    val primaryColor: Color,
    val errorColor: Color,
    val backgroundColor: Color
)

object ColorUtils {

    @Composable
    fun getDynamicColors(config: UiConfig?): DynamicColors {
        val hasBackgroundImage = config?.backgroundImagePath?.isNotEmpty() == true

        return DynamicColors(
            textColor = if (hasBackgroundImage) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textVariantColor = if (hasBackgroundImage) {
                Color.LightGray
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            primaryColor = if (hasBackgroundImage) {
                Color.White
            } else {
                MaterialTheme.colorScheme.primary
            },
            errorColor = if (hasBackgroundImage) {
                Color.Red
            } else {
                MaterialTheme.colorScheme.error
            },
            backgroundColor = if (hasBackgroundImage) {
                Color.Black.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
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