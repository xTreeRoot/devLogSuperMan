package org.treeroot.devlog.page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.json.model.UiConfig


@Composable
fun SqlEditor(
    value: String,
    onValueChange: (String) -> Unit,
    config: UiConfig? = null,
    // 新增执行SQL回调
    onExecuteSql: (() -> Unit)? = null,
    // 新增执行选中SQL回调
    onExecuteSelectedSql: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val state = remember { mutableStateOf(TextFieldValue(value)) }

    // 右键菜单状态
    val showContextMenu = remember { mutableStateOf(false) }
    var contextMenuPosition by remember { mutableStateOf(Offset.Zero) }

    // 选中的SQL文本
    var selectedSql by remember { mutableStateOf("") }
    // 当前Density获取屏幕密度
    val density = LocalDensity.current


    // 当外部 value 发生变化时，同步更新内部状态
    LaunchedEffect(value) {
        state.value = TextFieldValue(value, selection = state.value.selection)
    }


    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        // 只处理“右键按下”的瞬间
                        if (event.buttons.isSecondaryPressed) {
                            // 查找是否有指针刚刚按下（previousPressed = false, pressed = true）
                            val rightClickPress = event.changes.firstOrNull { change ->
                                !change.previousPressed && change.pressed
                            }
                            if (rightClickPress != null) {
                                showContextMenu.value = false // 先关闭
                                contextMenuPosition = rightClickPress.position
                                showContextMenu.value = true
                                rightClickPress.consume()
                                // 可选：消费整个事件的其他 change，避免干扰
                              //  event.changes.forEach { it.consume() }
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
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(rememberScrollState()),
            singleLine = false
        )
        SqlContextMenu(
            showContextMenu = showContextMenu,
            contextMenuPosition = contextMenuPosition,
            selectedSql = selectedSql,
            onExecuteSql = onExecuteSql,
            onExecuteSelectedSql = onExecuteSelectedSql,
            density = density
        )
    }
}
