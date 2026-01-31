package org.treeroot.devlog.page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.business.view.SqlFormatterViewModel
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.sql.db.DatabaseConnectionInfo
import org.treeroot.devlog.page.components.FormattedSqlDisplay
import org.treeroot.devlog.page.components.MessageDialog
import org.treeroot.devlog.page.components.sql.db.QueryResultDisplay
import org.treeroot.devlog.page.components.sql.SqlOperationButtons
import org.treeroot.devlog.page.enums.MessageType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SqlFormatterPage(viewModel: SqlFormatterViewModel, config: UiConfig? = null) {

    // 观察查询结果状态
    val queryResult by viewModel.queryResult


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {


        Text(
            text = "MyBatis SQL Formatter",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Row()
        {
            // sql 操作按钮
            SqlOperationButtons(viewModel = viewModel)
            // 空白距离
            Spacer(modifier = Modifier.width(16.dp))
            // 空白距离
            // 数据库连接信息
            DatabaseConnectionInfo(
                viewModel = viewModel,
                config = config,
            )
        }

        Column(modifier = Modifier.fillMaxHeight().weight(1f))
        // 格式化后的SQL显示区域和查询结果区域
        {
            // FormattedSqlDisplay 占三分之二的空间
            Box(modifier = Modifier.weight(1.75f)) {
                FormattedSqlDisplay(
                    formattedSql = viewModel.formattedSql.value,
                    config = config,
                    onExecuteSql = { viewModel.executeQuery(viewModel.formattedSql.value) },
                    onExecuteSelectedSql = { selected -> viewModel.executeQuery(selected) },
                    onValueChange = { newFormattedSql ->
                        viewModel.updateFormattedSql(newFormattedSql)
                    }
                )
            }
            if (queryResult != null) {
                // QueryResultDisplay 占三分之一的空间
                Box(modifier = Modifier.weight(1.25f)) {
                    QueryResultDisplay(
                        queryResult = queryResult,
                        config = config,
                        onExportClick = { /* 导出功能待实现 */ }
                    )
                }
            }
        }

// 即将删除
//        // 底部信息栏
//        BottomInfoBar(
//            originalSql = viewModel.originalSql.value,
//            isValid = viewModel.isValid.value,
//            config = config
//        )
    }
}