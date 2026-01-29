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
import org.treeroot.devlog.model.UiConfig
import org.treeroot.devlog.util.ColorUtils

@Composable
fun EditableJSONTextView(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {},
    config: UiConfig? = null
) {
    val scrollState = rememberScrollState()

    var textFieldValue by remember(text) {
        mutableStateOf(
            TextFieldValue(text)
        )
    }

    // 当外部 text 改变时，同步更新
    LaunchedEffect(text) {
        if (text != textFieldValue.text) {
            textFieldValue = TextFieldValue(text)
        }
    }

    // 获取动态颜色
    val dynamicColors = ColorUtils.getDynamicColors(config)

    Box(
        modifier = modifier
            .background(ColorUtils.getContainerBackgroundColor(config))
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
                .background(ColorUtils.getContainerBackgroundColor(config))
                .verticalScroll(scrollState),
            singleLine = false
        )
    }
}

