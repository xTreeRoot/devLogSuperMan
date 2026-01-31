package org.treeroot.devlog.page.components.sql

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.json.model.UiConfig
import androidx.compose.ui.graphics.Color
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

    // 获取选中的文本
    val selectedSql = remember {
        derivedStateOf {
            val textFieldValue = state.value
            val selectionStart = textFieldValue.selection.start
            val selectionEnd = textFieldValue.selection.end

            if (selectionStart != selectionEnd) {
                val minIndex = minOf(selectionStart, selectionEnd)
                val maxIndex = maxOf(selectionStart, selectionEnd)

                if (minIndex >= 0 && maxIndex <= textFieldValue.text.length) {
                    textFieldValue.text.substring(minIndex, maxIndex)
                } else {
                    ""
                }
            } else {
                ""
            }
        }
    }

    // 当外部 value 发生变化时，同步更新内部状态
    LaunchedEffect(value) {
        state.value = TextFieldValue(value, selection = state.value.selection)
    }

    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        ContextMenuDataProvider(
            items = {
                val menuItems = mutableListOf<ContextMenuItem>()

                // 添加执行完整SQL菜单项
                if (onExecuteSql != null) {
                    menuItems.add(
                        ContextMenuItem("执行完整SQL") {
                            onExecuteSql()
                        }
                    )
                }

                // 添加执行选中SQL菜单项
                if (selectedSql.value.isNotBlank() && onExecuteSelectedSql != null) {
                    menuItems.add(
                        ContextMenuItem("执行选中SQL") {
                            onExecuteSelectedSql(selectedSql.value)
                        }
                    )
                }

                menuItems
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
                singleLine = false,
                // 设置光标颜色
                cursorBrush = SolidColor(Color.Gray),
                        interactionSource = remember { MutableInteractionSource() }
            )
        }
    }
}