package org.treeroot.devlog.page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.treeroot.devlog.json.model.UiConfig
import org.treeroot.devlog.util.ColorUtils

@Composable
fun SqlEditor(
    value: String,
    onValueChange: (String) -> Unit,
    config: UiConfig? = null,
    onExecuteSql: (() -> Unit)? = null, // 新增执行SQL回调
    onExecuteSelectedSql: ((String) -> Unit)? = null, // 新增执行选中SQL回调
    modifier: Modifier = Modifier
) {
    val state = remember { mutableStateOf(TextFieldValue(value)) }

    // 右键菜单状态
    var showContextMenu by remember { mutableStateOf(false) }
    var contextMenuPosition by remember { mutableStateOf(Offset.Zero) }

    // 选中的SQL文本
    var selectedSql by remember { mutableStateOf("") }
    // 当前Density获取屏幕密度
    val density = LocalDensity.current


    // 当外部 value 发生变化时，同步更新内部状态
    LaunchedEffect(value) {
        state.value = TextFieldValue(value, selection = state.value.selection)
    }

    // 获取动态颜色
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Box(
        modifier = modifier
            .background(ColorUtils.getContainerBackgroundColor(config))
            .padding(8.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        // 检测释放事件
                        if (event.type == PointerEventType.Release) {
                            //  判断是否是右键释放
                            if (event.buttons.isSecondaryPressed) {
                                val change = event.changes.first()
                                // 获取选中文本
                                val selectionStart = state.value.selection.min
                                val selectionEnd = state.value.selection.max
                                selectedSql =
                                    if (selectionStart != selectionEnd) {
                                        state.value.text.substring(selectionStart, selectionEnd)
                                    } else {
                                        state.value.text
                                    }

                                contextMenuPosition = change.position
                                showContextMenu = true
                            }
                        }
                    }
                }
            }
    ) {
        BasicTextField(
            value = state.value,
            onValueChange = { newValue ->
                state.value = newValue
                onValueChange(newValue.text)
            },
            textStyle = TextStyle(
                fontSize = 15.sp,
                color = dynamicColors.textColor,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(ColorUtils.getContainerBackgroundColor(config))
                .verticalScroll(rememberScrollState()),
            singleLine = false
        )
        // 右键菜单
        DropdownMenu(
            expanded = showContextMenu,
            onDismissRequest = { showContextMenu = false },
            offset = with(density) {
                DpOffset(
                    contextMenuPosition.x.toDp(),
                    contextMenuPosition.y.toDp()
                )
            }
        ) {
            if (onExecuteSql != null) {
                DropdownMenuItem(
                    text = { Text("执行完整SQL") },
                    onClick = {
                        showContextMenu = false
                        onExecuteSql()
                    }
                )
            }

            // 如果有选中的SQL文本，添加执行选中SQL的选项
            if (selectedSql.isNotBlank() && onExecuteSelectedSql != null) {
                DropdownMenuItem(
                    text = { Text("执行选中SQL") },
                    onClick = {
                        showContextMenu = false
                        onExecuteSelectedSql(selectedSql)
                    }
                )
            }

            DropdownMenuItem(
                text = { Text("复制") },
                onClick = {
                    showContextMenu = false
                    // 复制逻辑由BasicTextField自动处理
                }
            )
            DropdownMenuItem(
                text = { Text("粘贴") },
                onClick = {
                    showContextMenu = false
                    // 粘贴逻辑由BasicTextField自动处理
                }
            )
            DropdownMenuItem(
                text = { Text("全选") },
                onClick = {
                    showContextMenu = false
                    // 全选逻辑由BasicTextField自动处理
                }
            )
        }
    }
}
