package org.treeroot.devlog.page.components.sql.db

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.business.view.SqlFormatterViewModel
import org.treeroot.devlog.json.model.UiConfig


/**
 * 数据库连接状态信息组件
 * 显示连接状态指示器和MySQL配置下拉菜单
 */
@Composable
fun DatabaseConnectionInfo(
    viewModel: SqlFormatterViewModel,
    config: UiConfig?,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "数据库连接: ",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
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
        // 空白距离
        Spacer(modifier = Modifier.width(16.dp))

        // MySQL配置下拉菜单
        MySqlConfigDropdown(viewModel)
    }
}

/**
 *  连接状态指示器组件
 */
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