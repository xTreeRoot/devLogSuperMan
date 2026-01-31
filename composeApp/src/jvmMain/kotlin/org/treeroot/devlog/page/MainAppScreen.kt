package org.treeroot.devlog.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.DevLog
import org.treeroot.devlog.business.view.EsDslViewModel
import org.treeroot.devlog.business.view.SqlFormatterViewModel
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.GlobalErrorHandler
import org.treeroot.devlog.service.ClipboardMonitorService
import org.treeroot.devlog.service.JsonStoreService
import org.treeroot.devlog.service.SystemTrayService
import org.treeroot.devlog.state.AppStateManager
import org.treeroot.devlog.util.ImageUtils

@Composable
fun MainApp() {

    // 从状态管理器获取配置，或者从数据库加载初始配置
    val stateConfig = AppStateManager.currentConfig
    val initialConfig = if (stateConfig != null) stateConfig else {
        val loadedConfig = JsonStoreService.loadUiConfig()
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
            ClipboardMonitorService.startMonitoring()
            SystemTrayService.updateTrayIconBasedOnStatus() // 更新托盘图标
            DevLog.info("剪贴板监控服务已启动")
        } else {
            ClipboardMonitorService.stopMonitoring()
            SystemTrayService.updateTrayIconBasedOnStatus() // 更新托盘图标
            DevLog.info("剪贴板监控服务已停止")
        }
    }

    // Sql ViewModel实例
    val sqlFormatterViewModel = remember { SqlFormatterViewModel() }
    // ES DSL ViewModel实例
    val esDslViewModel = remember { EsDslViewModel() }

    // 选中的标签页 - 使用rememberSaveable确保在重组时保持状态
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }


    // 全局错误处理器
    GlobalErrorHandler(
        sqlFormatterViewModel,
        esDslViewModel
    )

    // 在应用启动时自动激活默认的数据库配置
    LaunchedEffect(Unit) {
        sqlFormatterViewModel.autoActivateDefaultConfig()
    }

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
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
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
                    2 -> SettingsPage()
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
                .background(MaterialTheme.colorScheme.background)
        )
    }
}