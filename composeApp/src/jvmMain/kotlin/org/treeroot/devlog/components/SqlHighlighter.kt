package org.treeroot.devlog.components

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import java.util.regex.Pattern

@Composable
fun SqlHighlighter(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    val textFieldValue = remember(value) {
        mutableStateOf(TextFieldValue(value))
    }
    
    // 更新TextFieldValue当外部值改变时
    if (textFieldValue.value.text != value) {
        textFieldValue.value = textFieldValue.value.copy(text = value)
    }
    
    val highlightedText = buildAnnotatedString {
        appendHighlightedSql(value)
    }
    
    if (readOnly) {
        // 只读模式下显示高亮文本
        Text(
            text = highlightedText,
            fontFamily = FontFamily.Monospace,
            modifier = modifier
        )
    } else {
        BasicTextField(
            value = textFieldValue.value,
            onValueChange = { newValue ->
                textFieldValue.value = newValue
                onValueChange(newValue.text)
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions.Default,
            modifier = modifier
        )
    }
}

fun AnnotatedString.Builder.appendHighlightedSql(sql: String) {
    val upperSql = sql.uppercase()
    
    var lastIndex = 0
    val keywords = listOf(
        "SELECT", "FROM", "WHERE", "GROUP BY", "ORDER BY", "HAVING",
        "JOIN", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "OUTER JOIN",
        "UNION", "UNION ALL", "INSERT", "UPDATE", "DELETE", "CREATE",
        "ALTER", "DROP", "AS", "ON", "AND", "OR", "NOT", "IN", "EXISTS",
        "BETWEEN", "LIKE", "CASE", "WHEN", "THEN", "ELSE", "END",
        "LIMIT", "OFFSET", "VALUES", "SET", "INTO", "TABLE", "DATABASE",
        "PRIMARY KEY", "FOREIGN KEY", "REFERENCES", "CONSTRAINT", "INDEX",
        "DISTINCT", "COUNT", "SUM", "AVG", "MIN", "MAX", "IS NULL", "IS NOT NULL"
    )
    
    // 按长度排序关键词，以确保较长的关键词优先匹配
    val sortedKeywords = keywords.sortedByDescending { it.length }
    
    // 查找SQL关键词
    for (keyword in sortedKeywords) {
        val keywordUpper = keyword.uppercase()
        var startIndex = 0
        while (startIndex < upperSql.length) {
            val foundIndex = upperSql.indexOf(keywordUpper, startIndex)
            if (foundIndex != -1) {
                // 添加前面的文本
                if (foundIndex > lastIndex) {
                    append(sql.substring(lastIndex, foundIndex))
                }
                
                // 添加关键词（高亮）
                pushStyle(SpanStyle(color = Color(0xFF0000FF))) // 蓝色
                append(sql.substring(foundIndex, foundIndex + keyword.length))
                pop()
                
                lastIndex = foundIndex + keyword.length
                startIndex = lastIndex
            } else {
                break
            }
        }
    }
    
    // 添加剩余的文本
    if (lastIndex < sql.length) {
        // 检查是否有字符串字面量
        val remainingText = sql.substring(lastIndex)
        appendWithAdditionalHighlighting(remainingText)
    }
}

fun AnnotatedString.Builder.appendWithAdditionalHighlighting(text: String) {
    var i = 0
    var lastIndex = 0
    
    while (i < text.length) {
        when (text[i]) {
            '\'' -> {
                // 查找字符串结束
                val stringStart = i
                append(text.substring(lastIndex, stringStart))
                
                i++
                while (i < text.length && text[i] != '\'') {
                    if (i + 1 < text.length && text[i] == '\\' && text[i + 1] == '\'') {
                        i += 2 // 跳过转义的单引号
                    } else {
                        i++
                    }
                }
                
                if (i < text.length) {
                    i++ // 包含结束的单引号
                }
                
                pushStyle(SpanStyle(color = Color(0xFF00AA00))) // 绿色
                append(text.substring(stringStart, i))
                pop()
                
                lastIndex = i
            }
            '"' -> {
                // 查找双引号字符串结束
                val stringStart = i
                append(text.substring(lastIndex, stringStart))
                
                i++
                while (i < text.length && text[i] != '"') {
                    if (i + 1 < text.length && text[i] == '\\' && text[i + 1] == '"') {
                        i += 2 // 跳过转义的双引号
                    } else {
                        i++
                    }
                }
                
                if (i < text.length) {
                    i++ // 包含结束的双引号
                }
                
                pushStyle(SpanStyle(color = Color(0xFF00AA00))) // 绿色
                append(text.substring(stringStart, i))
                pop()
                
                lastIndex = i
            }
            else -> {
                i++
            }
        }
    }
    
    // 添加最后的文本
    if (lastIndex < text.length) {
        append(text.substring(lastIndex))
    }
}