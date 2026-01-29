package org.treeroot.devlog.logic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.treeroot.devlog.util.ClipboardHelper

/**
 * SQL格式化器的ViewModel
 * 管理UI状态和业务逻辑
 */
class SqlFormatterViewModel {

    private val formatterService = SqlFormatterService()

    // UI状态
    private val _originalSql = mutableStateOf("")
    val originalSql: State<String> = _originalSql

    private val _formattedSql = mutableStateOf("")
    val formattedSql: State<String> = _formattedSql

    private val _isValid = mutableStateOf(true)
    val isValid: State<Boolean> = _isValid

    private val _errorMessage = mutableStateOf("")

    @Suppress("unused")
    val errorMessage: State<String> = _errorMessage

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading


    /**
     * 格式化SQL
     */
    fun formatSql() {
        if (_isLoading.value) return // 防止重复点击

        CoroutineScope(Dispatchers.Default).launch {
            _isLoading.value = true
            _errorMessage.value = ""

            try {
                // 检测是否为MyBatis日志格式
                val result = if (formatterService.detectMybatisFormat(_originalSql.value)) {
                    formatterService.extractAndFormatMybatisSql(_originalSql.value)
                } else {
                    formatterService.formatSqlOneNodePerLine(_originalSql.value)
                }

                val isValid = formatterService.validateSql(result)

                _formattedSql.value = result
                _isValid.value = isValid

                if (!isValid && result != _originalSql.value) {
                    _errorMessage.value = "SQL语法可能存在问题，请检查括号匹配等"
                }
            } catch (e: Exception) {
                _errorMessage.value = "格式化过程中出现错误: ${e.message}"
                _isValid.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 使用MySQL Pretty格式化SQL
     */
    fun formatSqlWithPrettyStyle() {
        if (_isLoading.value) return // 防止重复点击

        CoroutineScope(Dispatchers.Default).launch {
            _isLoading.value = true
            _errorMessage.value = ""

            try {
                val result = formatterService.formatSqlWithPrettyStyle(_originalSql.value)

                val isValid = formatterService.validateSql(result)

                _formattedSql.value = result
                _isValid.value = isValid

                if (!isValid && result != _originalSql.value) {
                    _errorMessage.value = "SQL语法可能存在问题，请检查括号匹配等"
                }
            } catch (e: Exception) {
                _errorMessage.value = "格式化过程中出现错误: ${e.message}"
                _isValid.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }


    /**
     * 复制格式化后的SQL到剪贴板
     */
    fun copyFormattedSqlToClipboard() {
        if (_formattedSql.value.isNotEmpty()) {
            ClipboardHelper.copyToClipboard(_formattedSql.value)
        }
    }

    /**
     * 从剪贴板粘贴SQL到原始输入框
     */
    fun pasteFromClipboard() {
        val clipboardText = ClipboardHelper.getTextFromClipboard()
        if (!clipboardText.isNullOrEmpty()) {
            _originalSql.value = clipboardText
        }
    }
}