package org.treeroot.devlog.page.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.business.model.MySqlQueryResult
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.util.ColorUtils

/**
 * 查询结果显示区域组件
 * 显示查询结果表格、统计信息和错误信息
 */
@Composable
fun QueryResultDisplay(
    queryResult: MySqlQueryResult?,
    config: UiConfig?,
    onExportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ResultHeader(
            queryResult = queryResult,
            onExportClick = onExportClick,
            config = config
        )

        when {
            queryResult == null -> {
                // 显示空状态提示
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "等待执行查询",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            queryResult.success -> {
                SuccessResultContent(
                    queryResult = queryResult,
                    config = config
                )

                ResultStatistics(
                    queryResult = queryResult
                )
            }
            else -> {
                ErrorResultContent(queryResult = queryResult)
            }
        }
    }
}

@Composable
private fun ResultHeader(
    queryResult: MySqlQueryResult?,
    onExportClick: () -> Unit,
    config: UiConfig?
) {
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 导出结果按钮
//        Button(
//            onClick = onExportClick,
//            enabled = (queryResult?.success == true) && queryResult.data.isNotEmpty(),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.tertiary,
//                contentColor = MaterialTheme.colorScheme.onTertiary
//            )
//        ) {
//            Text("导出结果")
//        }
    }
}

@Composable
private fun SuccessResultContent(
    queryResult: MySqlQueryResult,
    config: UiConfig?
) {
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = ColorUtils.getContainerBackgroundColor(config)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (config?.backgroundImagePath?.isNotEmpty() == true) 0.dp else 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (queryResult.data.isNotEmpty()) {
            DataTable(
                columnNames = queryResult.columnNames,
                rowData = queryResult.data,
                config = config,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "查询返回空结果",
                    color = dynamicColors.textVariantColor
                )
            }
        }
    }
}

@Composable
private fun ResultStatistics(
    queryResult: MySqlQueryResult
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "行数: ${queryResult.rowCount}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "耗时: ${queryResult.queryTime} ms",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorResultContent(
    queryResult: MySqlQueryResult
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "错误: ${queryResult.errorMessage ?: "未知错误"}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(12.dp)
        )
    }
}