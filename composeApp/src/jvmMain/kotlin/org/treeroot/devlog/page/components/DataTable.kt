package org.treeroot.devlog.page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.util.ColorUtils

@Composable
fun DataTable(
    columnNames: List<String>,
    rowData: List<Map<String, Any?>>,
    columnWidths: List<Dp>? = null, // 新增：每列宽度（可选）
    config: UiConfig? = null,
    modifier: Modifier = Modifier
) {
    val dynamicColors = ColorUtils.getDynamicColors(config)

    // 如果未提供列宽，则统一使用默认宽度（如 120.dp）
    val defaultWidth = 120.dp
    val widths = columnWidths?.take(columnNames.size)?.let { list ->
        // 补齐长度不足的情况
        if (list.size < columnNames.size) {
            list + List(columnNames.size - list.size) { defaultWidth }
        } else {
            list
        }
    } ?: List(columnNames.size) { defaultWidth }

    // 共享的水平滚动状态（让表头和数据同步滚动）
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // === 表头 ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(dynamicColors.primaryColor.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .horizontalScroll(horizontalScrollState, enabled = true),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            columnNames.forEachIndexed { index, columnName ->
                Text(
                    text = columnName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = dynamicColors.primaryColor,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .width(widths[index])
                        .wrapContentWidth(Alignment.Start)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = DividerDefaults.color
        )

        // === 数据区域 ===
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(rowData) { index, row ->
                val isEvenRow = index % 2 == 0
                val backgroundColor = if (isEvenRow) {
                    dynamicColors.primaryColor.copy(alpha = 0.25f)
                } else {
                    dynamicColors.backgroundColor
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .horizontalScroll(horizontalScrollState, enabled = true), // 同步滚动
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    columnNames.forEachIndexed { colIndex, columnName ->
                        val cellValue = row[columnName]?.toString() ?: "NULL"
                        TooltipEllipsisText(
                            fullText = cellValue,
                            maxWidth = widths[colIndex],
                            color = dynamicColors.textColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}