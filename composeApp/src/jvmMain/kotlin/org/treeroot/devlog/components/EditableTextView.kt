package org.treeroot.devlog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontFamily
import org.treeroot.devlog.model.UiConfig
import org.treeroot.devlog.logic.EsDslFormatterService
import org.treeroot.devlog.util.ColorUtils

@Composable
fun EditableJSONTextView(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    config: UiConfig? = null
) {
    val esDslFormatter = remember { EsDslFormatterService() }
    val scrollState = rememberScrollState()

    // 初次进来就格式化 JSON
    var textFieldValue by remember(text) {
        mutableStateOf(
            TextFieldValue(
                if (text.isNotBlank()) esDslFormatter.formatJson(text) else text
            )
        )
    }

    // 当外部 text 改变时，也自动格式化
    LaunchedEffect(text) {
        val formatted = if (text.isNotBlank()) esDslFormatter.formatJson(text) else text
        if (formatted != textFieldValue.text) {
            textFieldValue = TextFieldValue(formatted)
            onValueChange(formatted) // 同步给外部 ViewModel
        }
    }

    // 获取动态颜色
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Box(
        modifier = modifier
            .background(ColorUtils.getComponentBackgroundColor(config))
            .padding(8.dp)
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onValueChange(newValue.text)
            },
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                color = dynamicColors.textColor
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(ColorUtils.getComponentBackgroundColor(config))
                .verticalScroll(scrollState),
            singleLine = false
        )
    }
}

