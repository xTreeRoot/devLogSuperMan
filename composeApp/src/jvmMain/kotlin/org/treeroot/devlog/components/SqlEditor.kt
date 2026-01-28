package org.treeroot.devlog.components

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.treeroot.devlog.DevLog

@Composable
fun SqlEditor(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var state by remember(value) { mutableStateOf(TextFieldValue(value)) }
    
    // 当外部 value 发生变化时，同步更新内部状态
    LaunchedEffect(value) {
        if (value != state.text) {
            state = TextFieldValue(value)
        }
    }
    
    DevLog.info("SqlEditor: $value")
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        BasicTextField(
            value = state,
            onValueChange = {
                state = it
                onValueChange(it.text)
            },
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            singleLine = false
        )
    }
}

/**
 *  纯粹的 MySQL SQL 格式化器，支持多条 SQL
 * 输出格式等同于:
 *
 * SELECT id,
 *        xx,
 *        yy
 * FROM table
 * WHERE ...
 * ORDER BY ...
 */
suspend fun formatMySqlPretty(raw: String): String = withContext(Dispatchers.Default) {

    raw.split(";")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString("\n\n") { sql ->
            formatSingleSql(sql)
        }
}

/**
 * 格式化单条 SQL
 */
private fun formatSingleSql(sql: String): String {
    var s = sql.trim()

    // 标准关键字前强制换行
    s = s.replace(Regex("(?i)\\bFROM\\b"), "\nFROM")
    s = s.replace(Regex("(?i)\\bWHERE\\b"), "\nWHERE")
    s = s.replace(Regex("(?i)\\bGROUP BY\\b"), "\nGROUP BY")
    s = s.replace(Regex("(?i)\\bORDER BY\\b"), "\nORDER BY")
    s = s.replace(Regex("(?i)\\bLEFT JOIN\\b"), "\nLEFT JOIN")
    s = s.replace(Regex("(?i)\\bRIGHT JOIN\\b"), "\nRIGHT JOIN")
    s = s.replace(Regex("(?i)\\bJOIN\\b"), "\nJOIN")

    // SELECT 字段格式化
    s = s.replace(Regex("(?i)SELECT"), "SELECT\n       ")

    // 逗号后换行并对齐
    s = s.replace(",", ",\n       ")

    // 多个空行压缩成 1 行
    s = s.replace(Regex("\n{3,}"), "\n\n")

    return s.trim()
}
