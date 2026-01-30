package org.treeroot.devlog.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.business.view.EnhancedSqlFormatterViewModel
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.page.components.*
import org.treeroot.devlog.util.ColorUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SqlFormatterPage(viewModel: EnhancedSqlFormatterViewModel, config: UiConfig? = null) {
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "MyBatis SQL Formatter",
            style = MaterialTheme.typography.headlineMedium,
            color = dynamicColors.primaryColor
        )

        // sql 操作按钮
        SqlOperationButtons(viewModel = viewModel)

        // 数据库连接信息
        DatabaseConnectionInfo(viewModel = viewModel, config = config)

        // 格式化后的SQL显示区域
        FormattedSqlDisplay(
            formattedSql = viewModel.formattedSql.value,
            config = config,
            onExecuteSql = { viewModel.executeQuery(viewModel.formattedSql.value) },
            onExecuteSelectedSql = { selected -> viewModel.executeQuery(selected) }
        )

        // 查询结果区域
        QueryResultDisplay(
            queryResult = viewModel.queryResult.value,
            config = config,
            onExportClick = { /* 导出功能待实现 */ }
        )

        // 底部信息栏
        BottomInfoBar(
            originalSql = viewModel.originalSql.value,
            isValid = viewModel.isValid.value,
            config = config
        )
    }
}