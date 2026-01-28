package org.treeroot.devlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.logic.EsDslViewModel
import org.treeroot.devlog.logic.SqlFormatterViewModel

@Composable
fun MainApp() {
    // 选中的标签页
    var selectedTab by remember { mutableStateOf(0) }
    // Sql ViewModel实例
    val sqlFormatterViewModel = remember { SqlFormatterViewModel() }
    // ES DSL ViewModel实例
    val esDslViewModel = remember { EsDslViewModel() }

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