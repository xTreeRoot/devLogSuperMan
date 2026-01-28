package org.treeroot.devlog.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.components.SqlEditor
import org.treeroot.devlog.logic.SqlFormatterViewModel
import org.treeroot.devlog.util.ColorUtils

@Composable
fun SqlFormatterPage(viewModel: SqlFormatterViewModel, config: org.treeroot.devlog.model.UiConfig? = null) {

    // 获取动态颜色
    val dynamicColors = ColorUtils.getDynamicColors(config)

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
            color = dynamicColors.primaryColor
        )

        // 按钮区域
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
        }

        // 格式化后SQL显示区域
        Text(
            text = "格式化后SQL:",
            style = MaterialTheme.typography.titleLarge,
            color = dynamicColors.textColor
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = ColorUtils.getContainerBackgroundColor(config)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                SqlEditor(
                    value = viewModel.formattedSql.value,
                    onValueChange = { /* 不允许直接编辑格式化后的SQL */ },
                    config = config,
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