package org.treeroot.devlog.page.components.setting
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.json.model.MySqlConfig
import org.treeroot.devlog.service.JsonStoreService

@Composable
 fun MySQLConfigurationCard(
    mysqlConfigs: List<MySqlConfig>,
    onShowAddMysqlDialogChanged: (Boolean) -> Unit,
    onDataChanged: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
            .clip(MaterialTheme.shapes.large),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxHeight(0.8f) // 设置卡片内容占最大高度的80%
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MySQL数据库配置",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(
                    onClick = { onShowAddMysqlDialogChanged(true) },
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("新增配置")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // MySQL配置列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                items(mysqlConfigs.size) { index ->
                    val mysqlConfig = mysqlConfigs[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (mysqlConfig.isDefault) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = mysqlConfig.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (mysqlConfig.isDefault) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                    Text(
                                        text = "${mysqlConfig.host}:${mysqlConfig.port}/${mysqlConfig.database}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (mysqlConfig.isDefault) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                    if (mysqlConfig.remarks.isNotEmpty()) {
                                        Text(
                                            text = mysqlConfig.remarks,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (mysqlConfig.isDefault) {
                                                MaterialTheme.colorScheme.onPrimaryContainer
                                            } else {
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            }
                                        )
                                    }
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (!mysqlConfig.isDefault) {
                                        Button(
                                            onClick = {
                                                JsonStoreService.setDefaultMySqlConfig(mysqlConfig.id)
                                                // 通知父组件数据已更改
                                                onDataChanged()
                                            },
                                            shape = MaterialTheme.shapes.small,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.tertiary,
                                                contentColor = MaterialTheme.colorScheme.onTertiary
                                            )
                                        ) {
                                            Text("设为默认")
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            JsonStoreService.deleteMySqlConfig(mysqlConfig.id)
                                            // 通知父组件数据已更改
                                            onDataChanged()
                                        },
                                        shape = MaterialTheme.shapes.small,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error,
                                            contentColor = MaterialTheme.colorScheme.onError
                                        )
                                    ) {
                                        Text("删除")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}