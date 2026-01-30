package org.treeroot.devlog.page.components.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.skottie.makeFromFile
import java.io.File
import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.asImageBitmap
@Composable
 fun AppearanceSettingsCard(
    isSystemAdaptive: Boolean,
    onSystemAdaptiveChanged: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    isSystemAdaptiveEnabled: Boolean,
    backgroundImagePath: String = "",
    onBackgroundImageClick: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .fillMaxHeight(0.8f), // 与MySQL配置卡片保持相同高度
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "外观设置",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 系统自适应主题开关
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "系统自适应主题",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    checked = isSystemAdaptive,
                    onCheckedChange = onSystemAdaptiveChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 手动主题选择（仅在非系统自适应时可用）
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "深色主题",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSystemAdaptiveEnabled) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Switch(
                    enabled = !isSystemAdaptiveEnabled, // 仅在非系统自适应时可用
                    checked = isDarkTheme,
                    onCheckedChange = onDarkThemeChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 背景图片预览
            // 背景图片预览
            if (backgroundImagePath.isNotEmpty()) {
                val file = File(backgroundImagePath)
                if (file.exists()) {
                    Text(
                        text = "当前背景图片:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // 显示背景图片缩略图
                    val imageBitmap = remember(backgroundImagePath) {
                        try {
                            Image.makeFromEncoded(File(backgroundImagePath).readBytes()).asImageBitmap()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                    imageBitmap?.let { bitmap ->
                        Image(
                            bitmap = bitmap,
                            contentDescription = "当前背景图片预览",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(vertical = 8.dp)              // 先 padding
                                .clip(MaterialTheme.shapes.medium)    // 再裁切
                                .clickable { onBackgroundImageClick(backgroundImagePath) }
                        )
                    }

                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}