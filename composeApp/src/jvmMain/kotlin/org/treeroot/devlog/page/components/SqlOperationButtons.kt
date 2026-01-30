package org.treeroot.devlog.page.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.business.view.SqlFormatterViewModel

/**
 * SQL操作按钮区域组件
 * 包含粘贴并解析、普通格式化和复制按钮
 */
@Composable
fun SqlOperationButtons(
    viewModel: SqlFormatterViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 粘贴并解析按钮
        PrimaryButton(
            onClick = { viewModel.pasteFromClipboard(); viewModel.formatSqlWithPrettyStyle() },
            enabled = !viewModel.isLoading.value,
            isLoading = viewModel.isLoading.value,
            modifier = Modifier.height(48.dp)
        )

        // 普通格式化按钮
        SecondaryButton(
            onClick = { viewModel.pasteFromClipboard(); viewModel.formatSql() },
            enabled = !viewModel.isLoading.value,
            isLoading = viewModel.isLoading.value,
            modifier = Modifier.height(48.dp)
        )

        // 复制按钮
        CopyButton(
            onClick = { viewModel.copyFormattedSqlToClipboard() },
            enabled = viewModel.formattedSql.value.isNotEmpty(),
            modifier = Modifier.height(48.dp)
        )
    }
}

@Composable
private fun PrimaryButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        if (isLoading) {
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
}

@Composable
private fun SecondaryButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        if (isLoading) {
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
}

@Composable
private fun CopyButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("复制")
    }
}