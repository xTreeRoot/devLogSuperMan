package org.treeroot.devlog.page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.business.view.EsDslViewModel
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.EditableJSONTextView


@Composable
fun EsDslPage(esViewModel: EsDslViewModel, config: UiConfig? = null) {

    // 获取动态颜色

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

        // 按钮区域
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { esViewModel.pasteFromClipboard(); esViewModel.formatDsl() },
                enabled = !esViewModel.isLoading.value,
                modifier = Modifier.height(48.dp),
                shape = MaterialTheme.shapes.medium,
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
                shape = MaterialTheme.shapes.medium
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(MaterialTheme.shapes.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (config?.backgroundImagePath?.isNotEmpty() == true) 0.dp else 4.dp)
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
                    color = MaterialTheme.colorScheme.onSurface
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(MaterialTheme.shapes.large),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (config?.backgroundImagePath?.isNotEmpty() == true) 0.dp else 4.dp)
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}