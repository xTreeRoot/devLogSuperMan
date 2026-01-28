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
import org.treeroot.devlog.components.EditableJSONTextView
import org.treeroot.devlog.model.UiConfig
import org.treeroot.devlog.logic.EsDslViewModel
import org.treeroot.devlog.util.ColorUtils

@Composable
fun EsDslPage(esViewModel: EsDslViewModel, config: UiConfig? = null) {

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
            text = "ES DSL 处理器",
            style = MaterialTheme.typography.headlineMedium,
            color = dynamicColors.primaryColor
        )

        // 按钮区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { esViewModel.pasteFromClipboard(); esViewModel.formatDsl() },
                enabled = !esViewModel.isLoading.value,
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(12.dp),
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
                    Text("解析中...")
                } else {
                    Text("粘贴并解析")
                }
            }

            OutlinedButton(
                onClick = { esViewModel.copyFormattedDslToClipboard() },
                enabled = esViewModel.formattedDsl.value.isNotEmpty(),
                modifier = Modifier.height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("复制")
            }
        }

        // DSL查询和解析结果的左右布局容器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // DSL查询显示区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "DSL 查询:",
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
                        EditableJSONTextView(
                            text = esViewModel.formattedDsl.value,
                            onValueChange = { newText ->
                                esViewModel.updateFormattedDsl(newText)
                            },
                            config = config,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // 解析后结果显示区域
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = "解析后结果:",
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
                        EditableJSONTextView(
                            text = esViewModel.formattedResponse.value,
                            onValueChange = { newText ->
                                esViewModel.updateFormattedResponse(newText)
                            },
                            config = config,
                            modifier = Modifier.fillMaxSize()
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
                text = "字符数: ${esViewModel.originalDsl.value.length}",
                style = MaterialTheme.typography.bodyLarge,
                color = dynamicColors.textVariantColor
            )
        }
    }
}