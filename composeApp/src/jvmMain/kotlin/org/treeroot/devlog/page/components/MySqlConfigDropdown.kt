package org.treeroot.devlog.page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.treeroot.devlog.business.view.SqlFormatterViewModel
import org.treeroot.devlog.service.JsonStoreService

/**
 * 数据库配置下拉菜单
 */
@Composable
fun MySqlConfigDropdown(
    viewModel: SqlFormatterViewModel
) {

    var expanded by remember { mutableStateOf(false) }

    // 使 allConfigs 能够响应外部变化
    val allConfigs by remember {
        derivedStateOf {
            JsonStoreService.getAllMySqlConfigs()
        }
    }

    // 使用 ViewModel 中的响应式状态
    val activeConfigId by viewModel.activeConfigId

    // 使用 derivedStateOf 来计算显示文本，这样会自动响应依赖项的变化
    val displayText by remember {
        derivedStateOf {
            val activeConfig = allConfigs.find { it.id == activeConfigId }
            activeConfig?.let { "${it.name}/${it.database} (${it.remarks})" } ?: "请选择数据库配置"
        }
    }

    // 搜索功能
    var searchQuery by remember { mutableStateOf("") }
    val filteredConfigs by remember(allConfigs, searchQuery) {
        derivedStateOf {
            allConfigs.filter { config ->
                searchQuery.isEmpty() ||
                        config.name.contains(searchQuery, ignoreCase = true) ||
                        config.host.contains(searchQuery, ignoreCase = true) ||
                        config.database.contains(searchQuery, ignoreCase = true) ||
                        config.remarks.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column {
        // 主按钮
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.widthIn(min = 180.dp)
        ) {
            Text(displayText)
        }

        // 下拉菜单
        if (expanded) {
            // Box + verticalScroll 避免 intrinsic measurement
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    filteredConfigs.forEach { config ->
                        DropdownMenuItem(
                            text = {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = config.name,
                                                    fontWeight = FontWeight.Medium,
                                                    color = if (config.isDefault) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurface
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
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}