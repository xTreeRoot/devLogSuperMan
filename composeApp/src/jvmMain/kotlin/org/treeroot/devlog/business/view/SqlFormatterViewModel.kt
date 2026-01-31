package org.treeroot.devlog.business.view

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.treeroot.devlog.DevLog
import org.treeroot.devlog.business.MySqlDatabaseService
import org.treeroot.devlog.business.SqlFormatterService
import org.treeroot.devlog.business.model.MySqlQueryResult
import org.treeroot.devlog.business.model.SqlFormatResult
import org.treeroot.devlog.json.model.MySqlConfig
import org.treeroot.devlog.mysql.MySqlConnectConfig
import org.treeroot.devlog.service.JsonStoreService
import org.treeroot.devlog.util.ClipboardHelper

/**
 * SQL格式化器的ViewModel
 */
class SqlFormatterViewModel : ViewModel(), ErrorCallbackHandler {

    private val formatterService = SqlFormatterService()
    private val databaseService = MySqlDatabaseService()

    // UI状态
    private val _originalSql = mutableStateOf("")
    val originalSql: State<String> = _originalSql

    private val _result = mutableStateOf(SqlFormatResult(success = true, originalSql = ""))
    val result: State<SqlFormatResult> = _result

    /**
     * 更新格式化后的SQL
     */
    fun updateFormattedSql(newFormattedSql: String) {
        _result.value = _result.value.copy(formattedSql = newFormattedSql)
    }

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

    private val _activeConfigId = mutableStateOf<String?>(null)
    val activeConfigId: State<String?> = _activeConfigId

    private val _allConfigs = mutableStateOf(JsonStoreService.getAllMySqlConfigs())
    val allConfigs: State<List<MySqlConfig>> = _allConfigs

    private val _queryResult = mutableStateOf<MySqlQueryResult?>(null)
    val queryResult: State<MySqlQueryResult?> = _queryResult

    private val _isExecuting = mutableStateOf(false)

    // 错误处理相关状态
    private val _onErrorCallback = mutableStateOf<((String) -> Unit)?>(null)

    // 计算显示文本的状态
    private val _displayText = mutableStateOf("请选择数据库配置")
    val displayText: State<String> = _displayText

    // 更新显示文本的方法
    fun updateDisplayText() {
        val activeConfig = _allConfigs.value.find { it.id == _activeConfigId.value }
        _displayText.value = if (activeConfig != null) {
            formatConfigDisplay(activeConfig)
        } else {
            // 尝试查找默认配置
            val allMySqlConfigs = JsonStoreService.getAllMySqlConfigs()
            val defaultConfig = allMySqlConfigs.find { it.isDefault }
            defaultConfig?.let { config ->
                formatConfigDisplay(config)
            } ?: "请选择数据库配置"
        }
    }

    // 格式化配置显示的私有辅助函数
    private fun formatConfigDisplay(config: MySqlConfig): String {
        return "${config.name}/${config.database} (${config.remarks})"
    }

    init {
        updateAllConfigs()
        updateDisplayText()
    }

    private fun updateAllConfigs() {
        _allConfigs.value = JsonStoreService.getAllMySqlConfigs()
    }

    /**
     * 刷新所有配置列表
     */
    fun refreshAllConfigs() {
        updateAllConfigs()
        updateDisplayText()
    }

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
                    errorMessage = "格式化过程中出现错误: ${e.message}",
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
                    errorMessage = "格式化过程中出现错误: ${e.message}",
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
                updateActiveConfigId() // 更新活跃配置ID
            } catch (e: Exception) {
                _connectionStatus.value = false
            }
        }
    }

    /**
     * 根据配置ID激活数据库连接
     */
    fun activateConnectionWithConfigId(configId: String) {
        viewModelScope.launch {
            try {
                val success = databaseService.activateConnectionWithConfigId(configId)
                if (success) {
                    val isConnected = databaseService.testConnection()
                    _connectionStatus.value = isConnected
                    // 更新活跃配置ID
                    updateActiveConfigId()
                } else {
                    _connectionStatus.value = false
                }
            } catch (e: Exception) {
                _connectionStatus.value = false
                // 通过错误回调通知UI
                _onErrorCallback.value?.invoke(e.message ?: "激活数据库配置时发生错误")
            }
        }
    }

    /**
     * 获取当前激活的配置ID
     */
    fun getActiveConfigId(): String? {
        return databaseService.getActiveConfigId()
    }

    private fun updateActiveConfigId() {
        _activeConfigId.value = databaseService.getActiveConfigId()
        updateAllConfigs() // 同时更新配置列表
        updateDisplayText() // 更新显示文本
    }

    /**
     * 自动激活默认的数据库配置
     */
    fun autoActivateDefaultConfig() {
        viewModelScope.launch {
            try {
                // 获取所有MySQL配置
                val allConfigs = JsonStoreService.getAllMySqlConfigs()

                // 查找默认配置
                val defaultConfig = allConfigs.find { it.isDefault }

                if (defaultConfig != null) {
                    // 使用找到的默认配置连接数据库
                    val connectConfig = MySqlConnectConfig(
                        host = defaultConfig.host,
                        port = defaultConfig.port,
                        database = defaultConfig.database,
                        username = defaultConfig.username,
                        password = defaultConfig.password
                    )

                    databaseService.initializeConnectionWithConfig(connectConfig)
                    val isConnected = databaseService.testConnection()
                    _connectionStatus.value = isConnected
                    updateActiveConfigId() // 更新活跃配置ID

                    DevLog.info("自动激活默认数据库配置: ${defaultConfig.name}, 连接${if (isConnected) "成功" else "失败"}")
                } else {
                    // 如果没有默认配置，但有配置存在，可以激活第一个配置
                    if (allConfigs.isNotEmpty()) {
                        val firstConfig = allConfigs.first()
                        val connectConfig = MySqlConnectConfig(
                            host = firstConfig.host,
                            port = firstConfig.port,
                            database = firstConfig.database,
                            username = firstConfig.username,
                            password = firstConfig.password
                        )

                        databaseService.initializeConnectionWithConfig(connectConfig)
                        val isConnected = databaseService.testConnection()
                        _connectionStatus.value = isConnected
                        // 更新活跃配置ID
                        updateActiveConfigId()

                        DevLog.info("激活首个数据库配置: ${firstConfig.name}, 连接${if (isConnected) "成功" else "失败"}")
                    }
                }
            } catch (e: Exception) {
                DevLog.error("自动激活默认数据库配置时发生错误: ${e.message}")
                _connectionStatus.value = false
            }
        }
    }

    /**
     * 执行SQL查询，这个方法的错误会显示在屏幕上，无须设置错误回调函数
     */
    fun executeQuery(sql: String) {
        if (!_connectionStatus.value) {
            // 如果没有连接，显示错误信息
            _onErrorCallback.value?.invoke("数据库未连接，请先连接数据库")
            return
        }

        _isExecuting.value = true
        viewModelScope.launch {
            try {
                val result = databaseService.query(sql)
                if (!result.success && result.errorMessage != null) {
                    DevLog.error("执行SQL查询时发生错误: ${result.errorMessage}")
                }
                _queryResult.value = result
            } catch (e: Exception) {
                _queryResult.value = MySqlQueryResult(
                    success = false,
                    errorMessage = e.message
                )
                DevLog.error("执行SQL查询时发生错误: ${e.message}")
            } finally {
                _isExecuting.value = false
            }
        }
    }

    /**
     * 设置错误回调函数
     */
    override fun setErrorCallback(errorCallback: (String) -> Unit) {
        _onErrorCallback.value = errorCallback
    }

    /**
     * 清除错误回调函数
     */
    override fun clearErrorCallback() {
        _onErrorCallback.value = null
    }

    /**
     * 更新默认配置后刷新配置列表
     */
    fun refreshConfigsAfterDefaultChange() {
        refreshAllConfigs()
    }
}