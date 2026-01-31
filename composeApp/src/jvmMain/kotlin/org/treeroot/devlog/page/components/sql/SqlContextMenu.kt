package org.treeroot.devlog.page.components.sql

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset

/**
 * 目前找不到干死BasicTextField 右键菜单的解决方案 使用ContextMenuDataProvider 直接提供菜单
 * - 右键点击事件：
 *
 * 使用 `.pointerInput(Unit)` 组合子组件时可以调用本逻辑：
 *
 * ```kotlin
 * .pointerInput(Unit) {
 *     awaitPointerEventScope {
 *         while (true) {
 *             val event = awaitPointerEvent()
 *             // 只处理“右键按下”的瞬间
 *             if (event.buttons.isSecondaryPressed) {
 *                 // 查找是否有指针刚刚按下（previousPressed = false, pressed = true）
 *                 val rightClickPress = event.changes.firstOrNull { change ->
 *                     !change.previousPressed && change.pressed
 *                 }
 *                 if (rightClickPress != null) {
 *                     // 关闭已有菜单
 *                     showContextMenu.value = false
 *                     // 记录右键点击位置
 *                     contextMenuPosition = rightClickPress.position
 *                     // 打开上下文菜单
 *                     showContextMenu.value = true
 *                     // 消费事件，防止其他组件处理
 *                     rightClickPress.consume()
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * 可复用右键菜单组件
 * @param showContextMenu 控制菜单显示的状态
 * @param contextMenuPosition 右键点击位置
 * @param selectedSql 当前选中的 SQL 文本
 * @param onExecuteSql 执行完整 SQL 的回调
 * @param onExecuteSelectedSql 执行选中 SQL 的回调
 * @param density 当前 Density，用于 DpOffset 转换
 */
@Deprecated("目前找不到干死BasicTextField 右键菜单的解决方案 使用ContextMenuDataProvider 直接提供菜单")
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