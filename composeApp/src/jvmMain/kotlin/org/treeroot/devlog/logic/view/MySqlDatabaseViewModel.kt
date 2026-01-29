package org.treeroot.devlog.logic.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.treeroot.devlog.logic.model.MySqlQueryResult
import org.treeroot.devlog.mysql.MySqlConnectConfig
import org.treeroot.devlog.logic.MySqlDatabaseService

/**
 * MySQL数据库操作的视图模型
 */
class MySqlDatabaseViewModel : ViewModel() {
    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus

    private val _queryResult = MutableStateFlow<MySqlQueryResult?>(null)
    val queryResult: StateFlow<MySqlQueryResult?> = _queryResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val databaseService = MySqlDatabaseService()

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

        _isLoading.value = true
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
                _isLoading.value = false
            }
        }
    }

    /**
     * 执行带参数的查询
     */
    fun executeQueryWithParams(sql: String, params: List<Any?>) {
        if (!_connectionStatus.value) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = databaseService.queryWithParams(sql, params)
                _queryResult.value = result
            } catch (e: Exception) {
                _queryResult.value = MySqlQueryResult(
                    success = false,
                    errorMessage = e.message
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 执行更新操作
     */
    fun executeUpdate(sql: String) {
        if (!_connectionStatus.value) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = databaseService.update(sql)
                _queryResult.value = result
            } catch (e: Exception) {
                _queryResult.value = MySqlQueryResult(
                    success = false,
                    errorMessage = e.message
                )
            } finally {
                _isLoading.value = false
            }
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
     * 测试数据库连接
     */
    fun testConnection() {
        viewModelScope.launch {
            val isConnected = databaseService.testConnection()
            _connectionStatus.value = isConnected
        }
    }
}