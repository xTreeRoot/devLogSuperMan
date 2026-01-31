package org.treeroot.devlog.page.components.sql.db

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.TooltipEllipsisText


@Composable
fun DataTable(
    columnNames: List<String>,
    rowData: List<Map<String, Any?>>,
    columnWidths: List<Dp>? = null,
    config: UiConfig? = null,
    modifier: Modifier = Modifier
) {

    val defaultWidth = 120.dp
    val widths = columnWidths?.take(columnNames.size)?.let { list ->
        if (list.size < columnNames.size) {
            list + List(columnNames.size - list.size) { defaultWidth }
        } else list
    } ?: List(columnNames.size) { defaultWidth }

    val horizontalScrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {


        //       表头
        PrimaryScrollableTabRow(
            selectedTabIndex = 0,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            indicator = {},
            divider = {}
        ) {
            columnNames.forEachIndexed { index, title ->
                Tab(
                    selected = false,
                    onClick = {},
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .width(widths[index])
                            .padding(8.dp)
                    )
                }
            }
        }

        HorizontalDivider()

        // === 数据 ===
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(rowData) { index, row ->
                val backgroundColor = if (index % 2 == 0)
                    MaterialTheme.colorScheme.surface
                else
                    MaterialTheme.colorScheme.surfaceVariant
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(backgroundColor)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .horizontalScroll(horizontalScrollState),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    columnNames.forEachIndexed { colIndex, name ->
                        TooltipEllipsisText(
                            fullText = row[name]?.toString() ?: "NULL",
                            maxWidth = widths[colIndex],
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}