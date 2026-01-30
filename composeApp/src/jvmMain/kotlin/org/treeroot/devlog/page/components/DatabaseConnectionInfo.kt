package org.treeroot.devlog.page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.business.view.EnhancedSqlFormatterViewModel
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.util.ColorUtils

/**
 * 数据库连接状态信息组件
 * 显示连接状态指示器和MySQL配置下拉菜单
 */
@Composable
fun DatabaseConnectionInfo(
    viewModel: EnhancedSqlFormatterViewModel,
    config: UiConfig?,
    modifier: Modifier = Modifier
) {
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "数据库连接: ",
            style = MaterialTheme.typography.bodyLarge,
            color = dynamicColors.textVariantColor
        )

        ConnectionIndicator(isConnected = viewModel.connectionStatus.value)

        Text(
            text = if (viewModel.connectionStatus.value) "已连接" else "未连接",
            style = MaterialTheme.typography.bodySmall,
            color = if (viewModel.connectionStatus.value) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.error
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        MySqlConfigDropdown(viewModel)
    }
}

@Composable
private fun ConnectionIndicator(isConnected: Boolean) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(
                if (isConnected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                shape = CircleShape
            )
    )
}