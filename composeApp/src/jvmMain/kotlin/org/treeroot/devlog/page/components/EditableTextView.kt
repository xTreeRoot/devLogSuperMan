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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.treeroot.devlog.json.model.UiConfig


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


    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
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
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .verticalScroll(scrollState),
            singleLine = false
        )
    }
}

