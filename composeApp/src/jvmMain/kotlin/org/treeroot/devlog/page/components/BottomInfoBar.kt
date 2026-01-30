package org.treeroot.devlog.page.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.treeroot.devlog.util.ColorUtils
import org.treeroot.devlog.json.model.UiConfig

/**
 * 底部信息栏组件
 * 显示SQL有效性状态和字符数统计
 */
@Composable
fun BottomInfoBar(
    originalSql: String,
    isValid: Boolean,
    config: UiConfig?,
    modifier: Modifier = Modifier
) {
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isValid) "SQL语法正常" else " SQL可能存在语法问题",
            color = if (isValid) dynamicColors.primaryColor else dynamicColors.errorColor,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "字符数: ${originalSql.length}",
            style = MaterialTheme.typography.bodyLarge,
            color = dynamicColors.textVariantColor
        )
    }
}