package org.treeroot.devlog.components

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
import androidx.compose.ui.input.pointer.PointerButtons
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
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
    modifier: Modifier = Modifier
) {
    val state = remember { mutableStateOf(TextFieldValue(value)) }

    // 右键菜单状态
    var showContextMenu by remember { mutableStateOf(false) }
    var contextMenuPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }

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
                        if (event.type == PointerEventType.Release) {
                            val change = event.changes.first()
//                            // 检查释放时是否是右键
//                            if (change.pressed.coerceIn(PointerButtons)) {
//                                contextMenuPosition = change.position
//                                showContextMenu = true
//                            }
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
            onDismissRequest = { showContextMenu = false }
        ) {
            if (onExecuteSql != null) {
                DropdownMenuItem(
                    text = { Text("执行SQL") },
                    onClick = {
                        showContextMenu = false
                        onExecuteSql()
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
