package org.treeroot.devlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.components.ImagePicker
import org.treeroot.devlog.json.model.MySqlConfig
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.mysql.MySqlConnectConfig
import org.treeroot.devlog.service.ClipboardMonitorService
import org.treeroot.devlog.service.JsonStoreService
import org.treeroot.devlog.state.AppStateManager
import org.treeroot.devlog.util.ColorUtils
import java.util.UUID

@Composable
fun SettingsPage(config: UiConfig? = null) {
    // 从数据库中加载初始值
    val initialConfig = remember { JsonStoreService.loadUiConfig() }

    var enableSilentMode by remember { mutableStateOf(initialConfig.enableClipboardMonitor) }
    var backgroundOpacity by remember { mutableStateOf(initialConfig.backgroundOpacity) }
    var backgroundImagePath by remember { mutableStateOf(initialConfig.backgroundImagePath) }

    // 获取当前配置，优先使用传入的参数
    val currentConfig = config ?: initialConfig

    // 获取动态颜色
    val dynamicColors = ColorUtils.getDynamicColors(currentConfig)

    // 更新数据库和状态管理器
    LaunchedEffect(enableSilentMode, backgroundOpacity, backgroundImagePath) {
        val newConfig = UiConfig(
            backgroundImagePath = backgroundImagePath,
            backgroundOpacity = backgroundOpacity,
            enableClipboardMonitor = enableSilentMode
        )
        JsonStoreService.saveConfigAsync(newConfig)
        AppStateManager.updateConfig(newConfig)
    }
    
    // MySQL配置管理相关状态
    var mysqlConfigs by remember { mutableStateOf(emptyList<MySqlConfig>()) }
    var showAddMysqlDialog by remember { mutableStateOf(false) }
    
    // 加载MySQL配置
    LaunchedEffect(Unit) {
        mysqlConfigs = JsonStoreService.getAllMySqlConfigs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 标题
        Text(
            text = "设置",
            style = MaterialTheme.typography.headlineMedium,
            color = dynamicColors.primaryColor
        )

        // 静默模式设置
        Card(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "静默模式",
                    style = MaterialTheme.typography.titleLarge,
                    color = dynamicColors.textColor
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "启用自动剪贴板监控",
                        style = MaterialTheme.typography.bodyLarge,
                        color = dynamicColors.textVariantColor
                    )
                    Switch(
                        checked = enableSilentMode,
                        onCheckedChange = { enableSilentMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // 透明度设置
        Card(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "界面透明度",
                    style = MaterialTheme.typography.titleLarge,
                    color = dynamicColors.textColor
                )

                Text(
                    "背景透明度: ${(backgroundOpacity * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = dynamicColors.textVariantColor
                )
                Slider(
                    value = backgroundOpacity,
                    onValueChange = { backgroundOpacity = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline
                    )
                )
            }
        }

        // 背景图片设置
        Card(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "背景图片",
                    style = MaterialTheme.typography.titleLarge,
                    color = dynamicColors.textColor
                )
                ImagePicker(
                    currentImagePath = backgroundImagePath,
                    onImageSelected = { selectedImagePath ->
                        backgroundImagePath = selectedImagePath

                        val newConfig = UiConfig(
                            backgroundImagePath = selectedImagePath,
                            backgroundOpacity = backgroundOpacity,
                            enableClipboardMonitor = enableSilentMode
                        )
                        JsonStoreService.saveConfigAsync(newConfig)
                        AppStateManager.updateConfig(newConfig)
                    },
                    buttonText = if (backgroundImagePath.isEmpty()) "选择背景图片" else "更改背景图片",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // MySQL配置管理
        Card(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MySQL数据库配置",
                        style = MaterialTheme.typography.titleLarge,
                        color = dynamicColors.textColor
                    )
                    Button(
                        onClick = { showAddMysqlDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("新增配置")
                    }
                }
                
                // MySQL配置列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .heightIn(max = 300.dp)
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
                                                    mysqlConfigs = JsonStoreService.getAllMySqlConfigs()
                                                },
                                                shape = RoundedCornerShape(6.dp),
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
                                                mysqlConfigs = JsonStoreService.getAllMySqlConfigs()
                                            },
                                            shape = RoundedCornerShape(6.dp),
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
    
    // 添加MySQL配置对话框
    if (showAddMysqlDialog) {
        var configName by remember { mutableStateOf("") }
        var host by remember { mutableStateOf("localhost") }
        var port by remember { mutableStateOf("3306") }
        var database by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var remarks by remember { mutableStateOf("") }
        
        AlertDialog(
            onDismissRequest = { showAddMysqlDialog = false },
            title = { Text("新增MySQL配置") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = configName,
                        onValueChange = { configName = it },
                        label = { Text("配置名称") },
                        placeholder = { Text("如：本地开发环境") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        label = { Text("主机地址") },
                        placeholder = { Text("localhost 或 IP 地址") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text("端口") },
                        placeholder = { Text("3306") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = database,
                        onValueChange = { database = it },
                        label = { Text("数据库名") },
                        placeholder = { Text("数据库名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("用户名") },
                        placeholder = { Text("数据库用户名") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("密码") },
                        placeholder = { Text("数据库密码") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text("备注") },
                        placeholder = { Text("配置说明") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (configName.isNotEmpty() && host.isNotEmpty() && 
                            port.isNotEmpty() && database.isNotEmpty() && 
                            username.isNotEmpty() && password.isNotEmpty()) {
                            val newConfig = MySqlConfig(
                                id = UUID.randomUUID().toString(),
                                name = configName,
                                host = host,
                                port = port.toIntOrNull() ?: 3306,
                                database = database,
                                username = username,
                                password = password,
                                isDefault = mysqlConfigs.isEmpty(), // 如果是第一个配置，设为默认
                                remarks = remarks
                            )
                            JsonStoreService.addMySqlConfig(newConfig)
                            mysqlConfigs = JsonStoreService.getAllMySqlConfigs()
                            showAddMysqlDialog = false
                            
                            // 重置表单
                            configName = ""
                            host = "localhost"
                            port = "3306"
                            database = ""
                            username = ""
                            password = ""
                            remarks = ""
                        }
                    },
                    enabled = configName.isNotEmpty() && host.isNotEmpty() && 
                             port.isNotEmpty() && database.isNotEmpty() && 
                             username.isNotEmpty() && password.isNotEmpty()
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddMysqlDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}