package org.treeroot.devlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.components.ImagePicker
import org.treeroot.devlog.model.UiConfig
import org.treeroot.devlog.service.ClipboardMonitorService
import org.treeroot.devlog.service.JsonStoreService
import org.treeroot.devlog.state.AppStateManager
import org.treeroot.devlog.util.ColorUtils

@Composable
fun SettingsPage(config: UiConfig? = null) {
    val clipboardMonitorService = remember { ClipboardMonitorService() }
    val jsonStoreService = remember { JsonStoreService() }

    // 从数据库中加载初始值
    val initialConfig = remember { jsonStoreService.loadUiConfig() }

    var enableSilentMode by remember { mutableStateOf(initialConfig.enableClipboardMonitor) }
    var backgroundOpacity by remember { mutableStateOf(initialConfig.backgroundOpacity) }
    var backgroundImagePath by remember { mutableStateOf(initialConfig.backgroundImagePath) }

    // 获取当前配置，优先使用传入的参数
    val currentConfig = config ?: initialConfig

    // 获取动态颜色
    val dynamicColors = ColorUtils.getDynamicColors(currentConfig)

    // 更新数据库和状态管理器
    LaunchedEffect(enableSilentMode, backgroundOpacity, backgroundImagePath) {
        val newConfig = UiConfig(
            backgroundImagePath = backgroundImagePath,
            backgroundOpacity = backgroundOpacity,
            enableClipboardMonitor = enableSilentMode
        )
        jsonStoreService.saveConfigAsync(newConfig)
        AppStateManager.updateConfig(newConfig)
    }

    LaunchedEffect(enableSilentMode) {
        if (enableSilentMode) {
            clipboardMonitorService.startMonitoring()
        } else {
            clipboardMonitorService.stopMonitoring()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 标题
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            color = dynamicColors.primaryColor
        )

        // 静默模式设置
        Card(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "静默模式",
                    style = MaterialTheme.typography.titleLarge,
                    color = dynamicColors.textColor
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "启用自动剪贴板监控",
                        style = MaterialTheme.typography.bodyLarge,
                        color = dynamicColors.textVariantColor
                    )
                    Switch(
                        checked = enableSilentMode,
                        onCheckedChange = { enableSilentMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // 透明度设置
        Card(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "界面透明度",
                    style = MaterialTheme.typography.titleLarge,
                    color = dynamicColors.textColor
                )

                Text(
                    "背景透明度: ${(backgroundOpacity * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = dynamicColors.textVariantColor
                )
                Slider(
                    value = backgroundOpacity,
                    onValueChange = { backgroundOpacity = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }

        // 背景图片设置
        Card(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "背景图片",
                    style = MaterialTheme.typography.titleLarge,
                    color = dynamicColors.textColor
                )
                ImagePicker(
                    currentImagePath = backgroundImagePath,
                    onImageSelected = { selectedImagePath ->
                        backgroundImagePath = selectedImagePath

                        val newConfig = UiConfig(
                            backgroundImagePath = selectedImagePath,
                            backgroundOpacity = backgroundOpacity,
                            enableClipboardMonitor = enableSilentMode
                        )
                        jsonStoreService.saveConfigAsync(newConfig)
                        AppStateManager.updateConfig(newConfig)
                    },
                    buttonText = if (backgroundImagePath.isEmpty()) "选择背景图片" else "更改背景图片",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}