package org.treeroot.devlog.page.components.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image
import org.treeroot.devlog.page.components.ImagePicker
import java.io.File

@Composable
fun BackgroundSettingsCard(
    backgroundOpacity: Float,
    onBackgroundOpacityChanged: (Float) -> Unit,
    backgroundImagePath: String,
    onBackgroundImageChanged: (String) -> Unit,
    onBackgroundImageClick: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .height(500.dp)
            .clip(MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "背景设置",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "背景透明度: ${(backgroundOpacity * 100).toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = backgroundOpacity,
                onValueChange = onBackgroundOpacityChanged,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ImagePicker(
                currentImagePath = backgroundImagePath,
                onImageSelected = onBackgroundImageChanged,
                buttonText = if (backgroundImagePath.isEmpty()) "选择背景图片" else "更改背景图片",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                            Image.makeFromEncoded(File(backgroundImagePath).readBytes()).toComposeImageBitmap()
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
                                // 先 padding
                                .padding(vertical = 8.dp)
                                .clip(MaterialTheme.shapes.medium)
                                // 再裁切
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