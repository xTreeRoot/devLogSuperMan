package org.treeroot.devlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.service.ClipboardMonitorService
import org.treeroot.devlog.service.DatabaseService
import org.treeroot.devlog.model.AppConfig
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun SettingsPage() {
    val clipboardMonitorService = remember { ClipboardMonitorService() }
    val databaseService = remember { DatabaseService() }
    
    // 从数据库中加载初始值
    val initialConfig = remember { databaseService.loadConfig() }
    
    var enableSilentMode by remember { mutableStateOf(initialConfig.enableClipboardMonitor) }
    var backgroundOpacity by remember { mutableStateOf(initialConfig.backgroundOpacity) }
    var backgroundImagePath by remember { mutableStateOf(initialConfig.backgroundImagePath) }
    
    // 更新数据库
    LaunchedEffect(enableSilentMode, backgroundOpacity, backgroundImagePath) {
        databaseService.saveConfigAsync(
            AppConfig(
                backgroundImagePath = backgroundImagePath,
                backgroundOpacity = backgroundOpacity,
                enableClipboardMonitor = enableSilentMode
            )
        )
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
            color = MaterialTheme.colorScheme.primary
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "启用自动剪贴板监控",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    "背景透明度: ${(backgroundOpacity * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = {
                        val fileChooser = javax.swing.JFileChooser()
                        fileChooser.fileFilter = FileNameExtensionFilter(
                            "图片文件", "jpg", "jpeg", "png", "gif", "bmp"
                        )
                        val result = fileChooser.showOpenDialog(null)
                        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                            val selectedFilePath = fileChooser.selectedFile.absolutePath
                            backgroundImagePath = selectedFilePath
                            
                            // 保存到数据库
                            databaseService.saveConfigAsync(
                                AppConfig(
                                    backgroundImagePath = selectedFilePath,
                                    backgroundOpacity = backgroundOpacity,
                                    enableClipboardMonitor = enableSilentMode
                                )
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(if (backgroundImagePath.isEmpty()) "选择背景图片" else "更改背景图片")
                }
                
                // 显示当前选择的图片路径
                if (backgroundImagePath.isNotEmpty()) {
                    val file = File(backgroundImagePath)
                    if (file.exists()) {
                        Text(
                            text = "已选择: ${file.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        Text(
                            text = "图片文件不存在，请重新选择",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        // 重置路径
                        backgroundImagePath = ""
                    }
                }
            }
        }
    }
}