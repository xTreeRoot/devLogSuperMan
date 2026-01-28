package org.treeroot.devlog.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SqlEditor(
    value: String,
    onValueChange: (String) -> Unit,
    config: org.treeroot.devlog.model.UiConfig? = null,
    modifier: Modifier = Modifier
) {
    val state = remember { mutableStateOf(TextFieldValue(value)) }

    // 当外部 value 发生变化时，同步更新内部状态
    LaunchedEffect(value) {
        state.value = TextFieldValue(value, selection = state.value.selection)
    }

    // 获取动态颜色
    val dynamicColors = org.treeroot.devlog.util.ColorUtils.getDynamicColors(config)

    Box(
        modifier = modifier
            .background(dynamicColors.backgroundColor)
            .padding(8.dp)
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
                .verticalScroll(rememberScrollState()),
            singleLine = false
        )
    }
}
