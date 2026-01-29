package org.treeroot.devlog.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.DevLog
import org.treeroot.devlog.logic.EsDslViewModel
import org.treeroot.devlog.logic.SqlFormatterViewModel
import org.treeroot.devlog.model.UiConfig
import org.treeroot.devlog.service.ClipboardMonitorService
import org.treeroot.devlog.service.JsonStoreService
import org.treeroot.devlog.state.AppStateManager
import org.treeroot.devlog.util.ImageUtils

@Composable
fun MainApp() {
    val jsonStoreService = remember { JsonStoreService() }
    val clipboardMonitorService = remember { ClipboardMonitorService() }

    // 从状态管理器获取配置，或者从数据库加载初始配置
    val stateConfig = AppStateManager.currentConfig
    val initialConfig = if (stateConfig != null) stateConfig else {
        val loadedConfig = jsonStoreService.loadUiConfig()
        AppStateManager.updateConfig(loadedConfig)
        loadedConfig
    }

    var config by remember { mutableStateOf(initialConfig) }

    // 监听来自状态管理器的配置更新
    LaunchedEffect(Unit) {
        AppStateManager.configUpdates.collect { newConfig ->
            config = newConfig
        }
    }

    // 根据配置启动或停止剪贴板监控服务
    LaunchedEffect(config.enableClipboardMonitor) {
        if (config.enableClipboardMonitor) {
            clipboardMonitorService.startMonitoring()
            DevLog.info("剪贴板监控服务已启动")
        } else {
            clipboardMonitorService.stopMonitoring()
            DevLog.info("剪贴板监控服务已停止")
        }
    }

    // 选中的标签页
    var selectedTab by remember { mutableStateOf(0) }
    // Sql ViewModel实例
    val sqlFormatterViewModel = remember { SqlFormatterViewModel() }
    // ES DSL ViewModel实例
    val esDslViewModel = remember { EsDslViewModel() }

    Box(modifier = Modifier.fillMaxSize()) {
        // 背景内容 - 使用独立的可组合函数避免标签页切换时的重组
        BackgroundLayer(config = config)

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 标签页选择器
            SecondaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (config.backgroundImagePath.isNotEmpty()) Color.Transparent else MaterialTheme.colorScheme.surface,
                contentColor = if (config.backgroundImagePath.isNotEmpty()) Color.White else MaterialTheme.colorScheme.onSurface,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "MyBatis SQL解析",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Elasticsearch DSL",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "设置",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (selectedTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }

            // 根据选择显示对应页面
            Box(
                modifier = Modifier.weight(1f)
            ) {
                when (selectedTab) {
                    0 -> SqlFormatterPage(viewModel = sqlFormatterViewModel, config = config)
                    1 -> EsDslPage(esViewModel = esDslViewModel, config = config)
                    2 -> SettingsPage(config = config)
                }
            }
        }
    }
}

/**
 * 背景层 - 独立的可组合函数，避免标签页切换时重组
 */
@Composable
private fun BackgroundLayer(config: UiConfig) {
    // 如果设置了背景图片，则显示背景图片
    if (config.backgroundImagePath.isNotEmpty()) {
        if (ImageUtils.isValidImageFile(config.backgroundImagePath)) {
            val imageBitmap = remember(config.backgroundImagePath) {
                ImageUtils.loadImageBitmap(config.backgroundImagePath)
            }
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                        .graphicsLayer(alpha = config.backgroundOpacity)
                )
            }
        }
    } else {
        Spacer(
            modifier = Modifier.fillMaxSize()
                .background(Color(0xFFF5F5F5))
        )
    }
}