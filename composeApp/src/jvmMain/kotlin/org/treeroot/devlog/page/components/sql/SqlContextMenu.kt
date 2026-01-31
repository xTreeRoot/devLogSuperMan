package org.treeroot.devlog.page.components.sql

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.Density

/**
 * 可复用右键菜单组件
 *
 * @param showContextMenu 控制菜单显示的状态
 * @param contextMenuPosition 右键点击位置
 * @param selectedSql 当前选中的 SQL 文本
 * @param onExecuteSql 执行完整 SQL 的回调
 * @param onExecuteSelectedSql 执行选中 SQL 的回调
 * @param density 当前 Density，用于 DpOffset 转换
 */
@Composable
fun SqlContextMenu(
    showContextMenu: MutableState<Boolean>,
    contextMenuPosition: Offset,
    selectedSql: String,
    onExecuteSql: (() -> Unit)? = null,
    onExecuteSelectedSql: ((String) -> Unit)? = null,
    density: Density
) {
    DropdownMenu(
        expanded = showContextMenu.value,
        onDismissRequest = { showContextMenu.value = false },
        offset = with(density) {
            DpOffset(
                x = contextMenuPosition.x.toDp(),
                y = contextMenuPosition.y.toDp()
            )
        }
    ) {
        // 执行完整 SQL
        if (onExecuteSql != null) {
            DropdownMenuItem(
                text = { Text("执行完整SQL") },
                onClick = {
                    showContextMenu.value = false
                    onExecuteSql()
                }
            )
        }

        // 执行选中 SQL
        if (selectedSql.isNotBlank() && onExecuteSelectedSql != null) {
            DropdownMenuItem(
                text = { Text("执行选中SQL") },
                onClick = {
                    showContextMenu.value = false
                    onExecuteSelectedSql(selectedSql)
                }
            )
        }

        // 基本编辑操作
        DropdownMenuItem(
            text = { Text("复制") },
            onClick = { showContextMenu.value = false }
        )
        DropdownMenuItem(
            text = { Text("粘贴") },
            onClick = { showContextMenu.value = false }
        )
        DropdownMenuItem(
            text = { Text("全选") },
            onClick = { showContextMenu.value = false }
        )
    }
}