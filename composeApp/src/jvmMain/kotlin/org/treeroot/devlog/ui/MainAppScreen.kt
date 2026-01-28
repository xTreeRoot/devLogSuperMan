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
import org.treeroot.devlog.logic.EsDslViewModel
import org.treeroot.devlog.logic.SqlFormatterViewModel
import org.treeroot.devlog.service.DatabaseService
import org.treeroot.devlog.state.AppStateManager
import org.treeroot.devlog.util.ImageUtils


@Composable
fun MainApp() {
    val databaseService = remember { DatabaseService() }

    // 从状态管理器获取配置，或者从数据库加载初始配置
    val stateConfig = AppStateManager.currentConfig
    val initialConfig = if (stateConfig != null) stateConfig else {
        val loadedConfig = databaseService.loadConfig()
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

    // 选中的标签页
    var selectedTab by remember { mutableStateOf(0) }
    // Sql ViewModel实例
    val sqlFormatterViewModel = remember { SqlFormatterViewModel() }
    // ES DSL ViewModel实例
    val esDslViewModel = remember { EsDslViewModel() }

    Box(modifier = Modifier.fillMaxSize()) {
        // 如果设置了背景图片，则显示背景图片
        if (config.backgroundImagePath.isNotEmpty()) {
            if (ImageUtils.isValidImageFile(config.backgroundImagePath)) {
                val imageBitmap = ImageUtils.loadImageBitmap(config.backgroundImagePath)
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
            // 如果没有背景图片，使用半透明白色覆盖层
            Spacer(
                modifier = Modifier.fillMaxSize()
                    .background(Color.White.copy(alpha = (1f - config.backgroundOpacity).coerceIn(0f, 0.5f)))
            )
        }

        // 半透明覆盖层确保内容可读性
        Spacer(
            modifier = Modifier.fillMaxSize()
                .background(Color.White.copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

                // 标签页选择器
        SecondaryTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(horizontal = 20.dp),
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
                0 -> SqlFormatterPage(viewModel = sqlFormatterViewModel)
                1 -> EsDslPage(esViewModel = esDslViewModel)
                2 -> SettingsPage()
            }
        }
    }
}
}