package org.treeroot.devlog.page.components.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.json.model.MySqlConfig
import java.util.UUID

@Composable
 fun MySQLConfigDialog(
    onSave: (MySqlConfig) -> Boolean,
    onCancel: () -> Unit
) {
    var configName by remember { mutableStateOf("") }
    var host by remember { mutableStateOf("localhost") }
    var port by remember { mutableStateOf("3306") }
    var database by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var remarks by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancel,
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
                    visualTransformation = PasswordVisualTransformation()
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
                        username.isNotEmpty() && password.isNotEmpty()
                    ) {
                        val newConfig = MySqlConfig(
                            id = UUID.randomUUID().toString(),
                            name = configName,
                            host = host,
                            port = port.toIntOrNull() ?: 3306,
                            database = database,
                            username = username,
                            password = password,
                            isDefault = true, // 新增配置默认设为默认
                            remarks = remarks
                        )
                        val shouldClose = onSave(newConfig)
                        if (shouldClose) {
                            onCancel()
                        } else {
                            // 重置表单
                            configName = ""
                            host = "localhost"
                            port = "3306"
                            database = ""
                            username = ""
                            password = ""
                            remarks = ""
                        }
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
                onClick = onCancel
            ) {
                Text("取消")
            }
        }
    )
}