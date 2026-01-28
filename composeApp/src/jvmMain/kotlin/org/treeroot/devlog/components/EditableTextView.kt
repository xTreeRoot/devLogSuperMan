import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontFamily
import org.treeroot.devlog.logic.EsDslFormatterService

@Composable
fun EditableJSONTextView(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit = {}
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
                .verticalScroll(scrollState),
            singleLine = false
        )
    }
}

