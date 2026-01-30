package org.treeroot.devlog.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.business.view.EnhancedSqlFormatterViewModel
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.DataTable
import org.treeroot.devlog.page.components.MySqlConfigDropdown
import org.treeroot.devlog.page.components.SqlEditor
import org.treeroot.devlog.util.ColorUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SqlFormatterPage(viewModel: EnhancedSqlFormatterViewModel, config: UiConfig? = null) {
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "MyBatis SQL Formatter",
            style = MaterialTheme.typography.headlineMedium,
            color = dynamicColors.primaryColor
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.pasteFromClipboard(); viewModel.formatSqlWithPrettyStyle() },
                enabled = !viewModel.isLoading.value,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("格式化中...")
                } else {
                    Text("粘贴并解析")
                }
            }

            Button(
                onClick = { viewModel.pasteFromClipboard(); viewModel.formatSql() },
                enabled = !viewModel.isLoading.value,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("解析中...")
                } else {
                    Text("普通格式化")
                }
            }

            OutlinedButton(
                onClick = { viewModel.copyFormattedSqlToClipboard() },
                enabled = viewModel.formattedSql.value.isNotEmpty(),
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("复制")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "数据库连接: ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = dynamicColors.textVariantColor
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            if (viewModel.connectionStatus.value) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error,
                            shape = CircleShape
                        )
                )
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

        Text("格式化后SQL:")

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = ColorUtils.getContainerBackgroundColor(config)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (config?.backgroundImagePath?.isNotEmpty() == true) 0.dp else 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                SqlEditor(
                    value = viewModel.formattedSql.value,
                    onValueChange = {},
                    config = config,
                    modifier = Modifier.fillMaxSize(),
                    onExecuteSql = { viewModel.executeQuery(viewModel.formattedSql.value) },
                    onExecuteSelectedSql = { selected -> viewModel.executeQuery(selected) }
                )
            }
        }

        // 查询结果区域
        if (viewModel.queryResult.value != null) {
            val result = viewModel.queryResult.value!!
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "查询结果:",
                        style = MaterialTheme.typography.titleLarge,
                        color = dynamicColors.textColor
                    )

                    // 导出结果按钮
                    Button(
                        onClick = { /* 导出功能待实现 */ },
                        enabled = result.success && result.data.isNotEmpty(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )
                    ) {
                        Text("导出结果")
                    }
                }

                if (result.success) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = ColorUtils.getContainerBackgroundColor(config)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (config?.backgroundImagePath?.isNotEmpty() == true) 0.dp else 4.dp)
                    ) {
                        if (result.data.isNotEmpty()) {
                            DataTable(
                                columnNames = result.columnNames,
                                rowData = result.data,
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

                    // 显示查询统计信息
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "行数: ${result.rowCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = dynamicColors.textVariantColor
                        )
                        Text(
                            text = "耗时: ${System.currentTimeMillis() - result.queryTime} ms",
                            style = MaterialTheme.typography.bodyMedium,
                            color = dynamicColors.textVariantColor
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = "错误: ${result.errorMessage ?: "未知错误"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // 底部信息栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (viewModel.isValid.value) "✓ SQL语法正常" else "⚠ SQL可能存在语法问题",
                color = if (viewModel.isValid.value) dynamicColors.primaryColor else dynamicColors.errorColor,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "字符数: ${viewModel.originalSql.value.length}",
                style = MaterialTheme.typography.bodyLarge,
                color = dynamicColors.textVariantColor
            )
        }
    }
}