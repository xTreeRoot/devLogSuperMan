package org.treeroot.devlog.business.view

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.treeroot.devlog.business.SqlFormatterService
import org.treeroot.devlog.business.MySqlDatabaseService
import org.treeroot.devlog.business.model.MySqlQueryResult
import org.treeroot.devlog.logic.model.SqlFormatResult
import org.treeroot.devlog.mysql.MySqlConnectConfig
import org.treeroot.devlog.util.ClipboardHelper

/**
 * 增强版SQL格式化器的ViewModel
 * 包含格式化功能和MySQL查询功能
 */
class EnhancedSqlFormatterViewModel : ViewModel() {

    private val formatterService = SqlFormatterService()
    private val databaseService = MySqlDatabaseService()

    // UI状态
    private val _originalSql = mutableStateOf("")
    val originalSql: State<String> = _originalSql

    private val _result = mutableStateOf(SqlFormatResult(success = true, originalSql = ""))
    val result: State<SqlFormatResult> = _result

    // 为了向后兼容，提供原有的属性访问
    val formattedSql: State<String> = object : State<String> {
        override val value: String
            get() = _result.value.formattedSql
    }

    val isValid: State<Boolean> = object : State<Boolean> {
        override val value: Boolean
            get() = _result.value.isValid
    }

    @Suppress("unused")
    val errorMessage: State<String> = object : State<String> {
        override val value: String
            get() = _result.value.errorMessage ?: ""
    }

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // MySQL查询相关状态
    private val _connectionStatus = mutableStateOf(false)
    val connectionStatus: State<Boolean> = _connectionStatus

    private val _queryResult = mutableStateOf<MySqlQueryResult?>(null)
    val queryResult: State<MySqlQueryResult?> = _queryResult

    private val _isExecuting = mutableStateOf(false)
    val isExecuting: State<Boolean> = _isExecuting

    /**
     * 格式化SQL
     */
    fun formatSql() {
        if (_isLoading.value) return // 防止重复点击

        CoroutineScope(Dispatchers.Default).launch {
            _isLoading.value = true

            try {
                val formattedSql = formatterService.smartFormatSql(_originalSql.value)

                val isValid = formatterService.validateSql(formattedSql)
                val success = true
                val errorMessage = if (!isValid && formattedSql != _originalSql.value) {
                    "SQL语法可能存在问题，请检查括号匹配等"
                } else {
                    null
                }

                _result.value = SqlFormatResult(
                    success = success,
                    formattedSql = formattedSql,
                    originalSql = _originalSql.value,
                    isValid = isValid,
                    processingTime = System.currentTimeMillis(),
                    errorMessage = errorMessage,
                    formatType = "smart"
                )
            } catch (e: Exception) {
                _result.value = SqlFormatResult(
                    success = false,
                    originalSql = _originalSql.value,
                    errorMessage = "格式化过程中出现错误: ${'$'}{e.message}",
                    isValid = false,
                    processingTime = System.currentTimeMillis()
                )
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

            try {
                val formattedSql = formatterService.formatSqlWithPrettyStyleSync(_originalSql.value)

                val isValid = formatterService.validateSql(formattedSql)
                val success = true
                val errorMessage = if (!isValid && formattedSql != _originalSql.value) {
                    "SQL语法可能存在问题，请检查括号匹配等"
                } else {
                    null
                }

                _result.value = SqlFormatResult(
                    success = success,
                    formattedSql = formattedSql,
                    originalSql = _originalSql.value,
                    isValid = isValid,
                    processingTime = System.currentTimeMillis(),
                    errorMessage = errorMessage,
                    formatType = "pretty"
                )
            } catch (e: Exception) {
                _result.value = SqlFormatResult(
                    success = false,
                    originalSql = _originalSql.value,
                    errorMessage = "格式化过程中出现错误: ${'$'}{e.message}",
                    isValid = false,
                    processingTime = System.currentTimeMillis()
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 复制格式化后的SQL到剪贴板
     */
    fun copyFormattedSqlToClipboard() {
        if (result.value.formattedSql.isNotEmpty()) {
            ClipboardHelper.copyToClipboard(result.value.formattedSql)
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

    /**
     * 连接到MySQL数据库
     */
    fun connectToDatabase(config: MySqlConnectConfig) {
        viewModelScope.launch {
            try {
                databaseService.initializeConnectionWithConfig(config)
                val isConnected = databaseService.testConnection()
                _connectionStatus.value = isConnected
            } catch (e: Exception) {
                _connectionStatus.value = false
            }
        }
    }

    /**
     * 执行SQL查询
     */
    fun executeQuery(sql: String) {
        if (!_connectionStatus.value) return

        _isExecuting.value = true
        viewModelScope.launch {
            try {
                val result = databaseService.query(sql)
                _queryResult.value = result
            } catch (e: Exception) {
                _queryResult.value = MySqlQueryResult(
                    success = false,
                    errorMessage = e.message
                )
            } finally {
                _isExecuting.value = false
            }
        }
    }

    /**
     * 测试数据库连接
     */
    fun testConnection() {
        viewModelScope.launch {
            val isConnected = databaseService.testConnection()
            _connectionStatus.value = isConnected
        }
    }

    /**
     * 断开数据库连接
     */
    fun disconnect() {
        databaseService.closeConnection()
        _connectionStatus.value = false
    }

    /**
     * 获取当前连接状态
     */
    fun getConnectionStatus(): Boolean {
        return _connectionStatus.value
    }
}