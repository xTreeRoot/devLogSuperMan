package org.treeroot.devlog.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.treeroot.devlog.business.view.EnhancedSqlFormatterViewModel
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.DataTable
import org.treeroot.devlog.page.components.SqlEditor
import org.treeroot.devlog.page.icon.MyIcons
import org.treeroot.devlog.service.JsonStoreService
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

                var expanded by remember { mutableStateOf(false) }
                val allConfigs by remember { mutableStateOf(JsonStoreService.getAllMySqlConfigs()) }
                val activeConfig = allConfigs.find { it.id == viewModel.getActiveConfigId() }
                val displayText = activeConfig?.let { "${it.name} (${it.host}:${it.port}/${it.database})" }
                    ?: "请选择数据库配置"

                // 搜索功能
                var searchQuery by remember { mutableStateOf("") }
                val filteredConfigs = allConfigs.filter { config ->
                    searchQuery.isEmpty() ||
                    config.name.contains(searchQuery, ignoreCase = true) ||
                    config.host.contains(searchQuery, ignoreCase = true) ||
                    config.database.contains(searchQuery, ignoreCase = true) ||
                    config.remarks.contains(searchQuery, ignoreCase = true)
                }

                Box {
                    OutlinedTextField(
                        value = displayText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .width(280.dp)
                            .clickable { expanded = true },
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    imageVector = if (expanded) MyIcons.ArrowUp else MyIcons.ArrowDown,
                                    contentDescription = "Expand"
                                )
                            }
                        }
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(320.dp)
                    ) {
                        // 搜索框
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("搜索数据库配置") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        // 分隔线
                        Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))

                        // 配置列表
                        Column(modifier = Modifier.verticalScroll(rememberScrollState()).heightIn(max = 300.dp)) {
                            filteredConfigs.forEach { config ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = config.name,
                                                            fontWeight = FontWeight.Medium,
                                                            color = if (config.isDefault) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                                        )
                                                        if (config.isDefault) {
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(
                                                                text = "(默认)",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                    }
                                                    Text(
                                                        text = "${config.host}:${config.port}/${config.database}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    if (config.remarks.isNotEmpty()) {
                                                        Text(
                                                            text = config.remarks,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }

                                                // 设为默认按钮
                                                if (!config.isDefault) {
                                                    TextButton(
                                                        onClick = {
                                                            JsonStoreService.setDefaultMySqlConfig(config.id)
                                                            // 重新加载配置
                                                            expanded = false
                                                        },
                                                        colors = ButtonDefaults.textButtonColors(
                                                            contentColor = MaterialTheme.colorScheme.primary
                                                        )
                                                    ) {
                                                        Text("设为默认", fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.activateConnectionWithConfigId(config.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
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
                            text = "行数: ${'$'}{result.rowCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = dynamicColors.textVariantColor
                        )
                        Text(
                            text = "耗时: ${'$'}{System.currentTimeMillis() - result.queryTime} ms",
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
                text = "字符数: ${'$'}{viewModel.originalSql.value.length}",
                style = MaterialTheme.typography.bodyLarge,
                color = dynamicColors.textVariantColor
            )
        }
    }
}