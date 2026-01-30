package org.treeroot.devlog.page

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.json.model.MySqlConfig
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.setting.*
import org.treeroot.devlog.service.JsonStoreService
import org.treeroot.devlog.state.AppStateManager

@Composable
fun SettingsPage() {
    // 从数据库中加载初始值
    val initialConfig = remember { JsonStoreService.loadUiConfig() }

    var enableSilentMode by remember { mutableStateOf(initialConfig.enableClipboardMonitor) }
    var backgroundOpacity by remember { mutableStateOf(initialConfig.backgroundOpacity) }
    var backgroundImagePath by remember { mutableStateOf(initialConfig.backgroundImagePath) }
    var isSystemAdaptive by remember { mutableStateOf(initialConfig.isSystemAdaptive) }
    var isDarkTheme by remember { mutableStateOf(initialConfig.isDarkTheme) }


    // 更新数据库和状态管理器
    LaunchedEffect(enableSilentMode, backgroundOpacity, backgroundImagePath, isSystemAdaptive, isDarkTheme) {
        val newConfig = UiConfig(
            backgroundImagePath = backgroundImagePath,
            backgroundOpacity = backgroundOpacity,
            enableClipboardMonitor = enableSilentMode,
            isSystemAdaptive = isSystemAdaptive,
            isDarkTheme = isDarkTheme
        )
        JsonStoreService.saveConfigAsync(newConfig)
        AppStateManager.updateConfig(newConfig)
    }

    // MySQL配置管理相关状态
    var mysqlConfigs by remember { mutableStateOf(emptyList<MySqlConfig>()) }
    var showAddMysqlDialog by remember { mutableStateOf(false) }

    // 加载MySQL配置
    LaunchedEffect(Unit) {
        mysqlConfigs = JsonStoreService.getAllMySqlConfigs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
    ) {
        // 标题
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 四列网格布局
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ClipboardMonitoringCard(
                        enableSilentMode = enableSilentMode,
                        onEnableSilentModeChanged = { enableSilentMode = it }
                    )
                }

                item {
                    AppearanceSettingsCard(
                        isSystemAdaptive = isSystemAdaptive,
                        onSystemAdaptiveChanged = { newValue ->
                            isSystemAdaptive = newValue
                            // 如果启用系统自适应，则忽略手动主题选择
                            if (newValue) isDarkTheme = false
                        },
                        isDarkTheme = isDarkTheme,
                        onDarkThemeChanged = { isDarkTheme = it },
                        isSystemAdaptiveEnabled = isSystemAdaptive,
                        backgroundImagePath = backgroundImagePath,
                        onBackgroundImageClick = { imagePath ->
                            // 这里可以处理图片点击事件，比如打开图片预览
                            println("点击了背景图片: $imagePath")
                        }
                    )
                }

                item {
                    BackgroundSettingsCard(
                        backgroundOpacity = backgroundOpacity,
                        onBackgroundOpacityChanged = { backgroundOpacity = it },
                        backgroundImagePath = backgroundImagePath,
                        onBackgroundImageChanged = { selectedImagePath ->
                            backgroundImagePath = selectedImagePath

                            val newConfig = UiConfig(
                                backgroundImagePath = selectedImagePath,
                                backgroundOpacity = backgroundOpacity,
                                enableClipboardMonitor = enableSilentMode,
                                isSystemAdaptive = isSystemAdaptive,
                                isDarkTheme = isDarkTheme
                            )
                            JsonStoreService.saveConfigAsync(newConfig)
                            AppStateManager.updateConfig(newConfig)
                        }
                    )
                }

                item {
                    MySQLConfigurationCard(
                        mysqlConfigs = mysqlConfigs,
                        onShowAddMysqlDialogChanged = { showAddMysqlDialog = it },
                        onDataChanged = {
                            // 数据更改时重新加载MySQL配置
                            mysqlConfigs = JsonStoreService.getAllMySqlConfigs()
                        }
                    )
                }
            }
        }
    }

    // 添加MySQL配置对话框
    if (showAddMysqlDialog) {
        MySQLConfigDialog(
            onSave = { newConfig ->
                JsonStoreService.addMySqlConfig(newConfig)
                // 重新加载MySQL配置
                mysqlConfigs = JsonStoreService.getAllMySqlConfigs()
                true // 关闭对话框
            },
            onCancel = { showAddMysqlDialog = false }
        )
    }
}