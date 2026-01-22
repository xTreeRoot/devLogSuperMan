package org.treeroot.devlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.theme.DevLogTheme

@Composable
fun App() {
    DevLogTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    var selectedTab by remember { mutableStateOf(0) }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 标签页选择器
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.padding(horizontal = 20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { 
                    Text(
                        "MyBatis SQL",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selectedTab == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { 
                    Text(
                        "ES DSL",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selectedTab == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { 
                    Text(
                        "设置",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (selectedTab == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.padding(vertical = 12.dp)
            )
        }
        
        // 根据选择显示对应页面
        Box(
            modifier = Modifier.weight(1f)
        ) {
            when (selectedTab) {
                0 -> SqlFormatterPage()
                1 -> EsDslPage()
                2 -> SettingsPage()
            }
        }
    }
}

@Composable
fun SqlFormatterPage() {
    val viewModel = remember { org.treeroot.devlog.logic.SqlFormatterViewModel() }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 标题
        Text(
            text = "MyBatis SQL Formatter",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        // 原始SQL输入区域
        Text(
            text = "原始SQL:",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        OutlinedTextField(
            value = viewModel.originalSql.value,
            onValueChange = { viewModel.updateOriginalSql(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
            placeholder = {
                Text("请输入SQL语句或MyBatis日志...")
            },
            maxLines = 10,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        )
        
        // 控制按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.formatSql() },
                enabled = !viewModel.isLoading.value,
                modifier = Modifier.height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
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
                    Text("格式化SQL")
                }
            }
            
            OutlinedButton(
                onClick = { viewModel.clearAll() },
                modifier = Modifier.height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("清空")
            }
            
            OutlinedButton(
                onClick = { viewModel.pasteFromClipboard() },
                modifier = Modifier.height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("粘贴")
            }
            
            OutlinedButton(
                onClick = { viewModel.copyFormattedSqlToClipboard() },
                enabled = viewModel.formattedSql.value.isNotEmpty(),
                modifier = Modifier.height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("复制")
            }
            
            // 显示验证状态
            if (viewModel.errorMessage.value.isNotEmpty()) {
                Text(
                    text = viewModel.errorMessage.value,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        // 格式化后SQL显示区域
        Text(
            text = "格式化后SQL:",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                org.treeroot.devlog.components.SqlHighlighter(
                    value = viewModel.formattedSql.value,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxSize()
                )
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
                color = if (viewModel.isValid.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = "字符数: ${viewModel.originalSql.value.length}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun EsDslPage() {
    val esViewModel = remember { org.treeroot.devlog.logic.EsDslViewModel() }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 标题
        Text(
            text = "ES DSL 处理器",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        
        // DSL和结果分栏布局
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // DSL区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "DSL 查询:",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    // 显示格式化文本或树形视图
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        if (esViewModel.showDslTree.value) {
                            org.treeroot.devlog.components.JsonTreeView(
                                jsonString = esViewModel.formattedDsl.value,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = esViewModel.formattedDsl.value,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
            
            // 结果区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "解析结果:",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    // 显示格式化文本或树形视图
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        if (esViewModel.showResultTree.value) {
                            org.treeroot.devlog.components.JsonTreeView(
                                jsonString = esViewModel.formattedResponse.value,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Text(
                                text = esViewModel.formattedResponse.value,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
        
        // 原始ES DSL输入区域
        Text(
            text = "原始ES DSL:",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        OutlinedTextField(
            value = esViewModel.originalDsl.value,
            onValueChange = { esViewModel.updateOriginalDsl(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
            placeholder = {
                Text("请输入ES DSL JSON或curl命令...")
            },
            maxLines = 10,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        )
        
        // 控制按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { esViewModel.formatDsl() },
                enabled = !esViewModel.isLoading.value,
                modifier = Modifier.height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (esViewModel.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("格式化中...")
                } else {
                    Text("格式化ES DSL")
                }
            }
            
            OutlinedButton(
                onClick = { esViewModel.clearAll() },
                modifier = Modifier.height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("清空")
            }
            
            OutlinedButton(
                onClick = { esViewModel.pasteFromClipboard() },
                modifier = Modifier.height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("粘贴")
            }
            
            OutlinedButton(
                onClick = { esViewModel.copyFormattedDslToClipboard() },
                enabled = esViewModel.formattedDsl.value.isNotEmpty(),
                modifier = Modifier.height(48.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text("复制")
            }
        }
        
        // 底部信息栏
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "字符数: ${esViewModel.originalDsl.value.length}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsPage() {
    val clipboardMonitorService = remember { org.treeroot.devlog.service.ClipboardMonitorService() }
    var enableSilentMode by remember { mutableStateOf(clipboardMonitorService.isMonitoring()) }
    var backgroundOpacity by remember { mutableStateOf(1f) }
    
    LaunchedEffect(enableSilentMode) {
        if (enableSilentMode) {
            clipboardMonitorService.startMonitoring()
        } else {
            clipboardMonitorService.stopMonitoring()
        }
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
            color = MaterialTheme.colorScheme.primary
        )
        
        // 静默模式设置
        Card(
            modifier = Modifier.fillMaxWidth()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("启用自动剪贴板监控",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text("背景透明度: ${(backgroundOpacity * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Button(
                    onClick = {
                        val fileChooser = javax.swing.JFileChooser()
                        fileChooser.fileFilter = javax.swing.filechooser.FileNameExtensionFilter(
                            "图片文件", "jpg", "jpeg", "png", "gif", "bmp"
                        )
                        val result = fileChooser.showOpenDialog(null)
                        if (result == javax.swing.JFileChooser.APPROVE_OPTION) {
                            // 在这里可以保存选择的图片路径到状态变量
                            println("选择了背景图片: ${fileChooser.selectedFile.absolutePath}")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                        .height(48.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("选择背景图片")
                }
            }
        }
    }
}