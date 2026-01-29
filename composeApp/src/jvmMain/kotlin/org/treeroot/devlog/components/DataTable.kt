package org.treeroot.devlog.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.util.ColorUtils

@Composable
fun DataTable(
    columnNames: List<String>,
    rowData: List<Map<String, Any?>>,
    config: UiConfig? = null,
    modifier: Modifier = Modifier
) {
    val dynamicColors = ColorUtils.getDynamicColors(config)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 表头
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(dynamicColors.primaryColor.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            columnNames.forEach { columnName ->
                Text(
                    text = columnName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = dynamicColors.primaryColor,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.width(IntrinsicSize.Max)
                )
            }
        }

        // 分割线
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = DividerDefaults.color
        )

        // 数据行
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
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    columnNames.forEach { columnName ->
                        val cellValue = row[columnName]?.toString() ?: "NULL"
                        Text(
                            text = cellValue,
                            fontSize = 12.sp,
                            color = dynamicColors.textColor,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.width(IntrinsicSize.Max)
                        )
                    }
                }
            }
        }

        // 滚动条
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.End),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}