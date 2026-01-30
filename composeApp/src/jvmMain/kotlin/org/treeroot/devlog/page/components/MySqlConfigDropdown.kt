package org.treeroot.devlog.page.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import org.treeroot.devlog.business.view.SqlFormatterViewModel
import org.treeroot.devlog.service.JsonStoreService

/**
 * 数据库配置下拉菜单（优化版 - 移除搜索框）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySqlConfigDropdown(
    viewModel: SqlFormatterViewModel,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val allConfigs by viewModel.allConfigs
    val activeConfigId by viewModel.activeConfigId
    val displayText by viewModel.displayText

    LaunchedEffect(activeConfigId) {
        if (activeConfigId != null) {
            viewModel.refreshAllConfigs()
        }
    }

    // 主按钮
    OutlinedButton(
        onClick = { expanded = true },
        modifier = modifier.widthIn(min = 180.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Text(displayText)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(0.dp, 20.dp),
            modifier = Modifier
                .widthIn(min = 180.dp, max = 360.dp)
                .heightIn(max = 300.dp),
            scrollState = rememberScrollState(),
            containerColor = Color.White,
            properties = PopupProperties(focusable = true) // 允许 TextField 获取焦点
        ) {
            if (allConfigs.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("无可用配置", style = MaterialTheme.typography.bodyMedium) },
                    enabled = false,
                    onClick = { /* no-op */ }
                )
            } else {
                allConfigs.forEach { config ->
                    DropdownMenuItem(
                        onClick = {
                            viewModel.activateConnectionWithConfigId(config.id)
                            expanded = false
                        },
                        modifier = Modifier.widthIn(800.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = config.name,
                                        fontWeight = FontWeight.Medium,
                                        color = if (config.isDefault) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
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

                            // "设为默认"按钮（仅非默认项显示）
                            if (!config.isDefault) {
                                TextButton(
                                    onClick = {
                                        JsonStoreService.setDefaultMySqlConfig(config.id)
                                        // 刷新配置列表以反映新的默认状态
                                        viewModel.refreshConfigsAfterDefaultChange()
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
                    if (allConfigs.indexOf(config) < allConfigs.lastIndex) {
                        HorizontalDivider(
                            color = Color.Blue,
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }


}