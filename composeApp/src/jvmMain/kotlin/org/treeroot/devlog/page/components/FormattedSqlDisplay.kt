package org.treeroot.devlog.page.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.sql.SqlEditor


/**
 * 格式化SQL显示区域组件
 * 包含标题和SQL编辑器
 */
@Composable
fun FormattedSqlDisplay(
    formattedSql: String,
    config: UiConfig?,
    onExecuteSql: (String) -> Unit,
    onExecuteSelectedSql: (String) -> Unit,
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (config?.backgroundImagePath?.isNotEmpty() == true) 0.dp else 4.dp
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            SqlEditor(
                value = formattedSql,
                onValueChange = onValueChange,
                config = config,
                modifier = Modifier.fillMaxSize(),
                onExecuteSql = { onExecuteSql(formattedSql) },
                onExecuteSelectedSql = onExecuteSelectedSql,
            )
        }
    }
}