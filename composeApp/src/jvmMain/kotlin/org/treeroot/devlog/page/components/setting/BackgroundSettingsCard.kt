package org.treeroot.devlog.page.components.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.page.components.ImagePicker

@Composable
 fun BackgroundSettingsCard(
    backgroundOpacity: Float,
    onBackgroundOpacityChanged: (Float) -> Unit,
    backgroundImagePath: String,
    onBackgroundImageChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
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
        }
    }
}