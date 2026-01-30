package org.treeroot.devlog.mysql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.treeroot.devlog.business.model.MySqlQueryResult
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import javax.sql.DataSource

/**
 * MySQL数据库连接管理器
 * 提供连接池管理和查询功能
 */
class MySqlDatabaseManager {
    private var dataSource: DataSource? = null

    /**
     * 初始化数据库连接池
     */
    fun initializeConnectionPool(config: MySqlConnectConfig) {
        val configBuilder = HikariConfig().apply {
            jdbcUrl = "jdbc:mysql://${config.host}:${config.port}/${config.database}?useSSL=${config.useSSL}&serverTimezone=UTC&characterEncoding=utf8&connectTimeout=${config.connectTimeout}&socketTimeout=${config.socketTimeout}"
            this.username = config.username
            this.password = config.password
            // 连接池配置
            maximumPoolSize = config.poolSize
            minimumIdle = 2
            // 连接超时配置  5分钟
            idleTimeout = 300000
            // 连接最大生命周期  10分钟
            maxLifetime = 600000
            // 连接超时配置  根据配置设置超时时间
            connectionTimeout = config.connectTimeout.toLong()
            // 连接验证超时配置  5秒
            validationTimeout = 5000

            // 其他优化设置
            connectionTestQuery = "SELECT 1"
            isAutoCommit = true
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            addDataSourceProperty("useServerPrepStmts", "true")
            addDataSourceProperty("useLocalSessionState", "true")
            addDataSourceProperty("rewriteBatchedStatements", "true")
            addDataSourceProperty("maintainTimeStats", "false")
        }

        dataSource = HikariDataSource(configBuilder)
    }

    /**
     * 获取数据库连接
     */
    fun getConnection(): Connection? {
        return dataSource?.connection
    }

    /**
     * 执行查询语句
     */
    fun executeQuery(sql: String): MySqlQueryResult {
        return try {
            val connection = getConnection() ?: throw SQLException("无法获取数据库连接")

            val startTime = System.currentTimeMillis()
            val resultSet = connection.prepareStatement(sql).executeQuery()
            val result = processResultSet(resultSet)
            val queryTime = System.currentTimeMillis() - startTime

            connection.close()

            MySqlQueryResult(
                success = true,
                data = result.first,
                columnNames = result.second,
                rowCount = result.first.size,
                queryTime = queryTime
            )
        } catch (e: Exception) {
            MySqlQueryResult(
                success = false,
                errorMessage = e.message
            )
        }
    }

    /**
     * 执行更新语句（INSERT, UPDATE, DELETE）
     */
    fun executeUpdate(sql: String): MySqlQueryResult {
        return try {
            val connection = getConnection() ?: throw SQLException("无法获取数据库连接")

            val startTime = System.currentTimeMillis()
            val statement = connection.prepareStatement(sql)
            val affectedRows = statement.executeUpdate()
            val queryTime = System.currentTimeMillis() - startTime

            connection.close()

            MySqlQueryResult(
                success = true,
                affectedRows = affectedRows,
                queryTime = queryTime
            )
        } catch (e: Exception) {
            MySqlQueryResult(
                success = false,
                errorMessage = e.message
            )
        }
    }

    /**
     * 执行参数化查询
     */
    fun executeQueryWithParams(sql: String, params: List<Any?>): MySqlQueryResult {
        return try {
            val connection = getConnection() ?: throw SQLException("无法获取数据库连接")

            val startTime = System.currentTimeMillis()
            val statement = prepareStatementWithParams(connection, sql, params)
            val resultSet = statement.executeQuery()
            val result = processResultSet(resultSet)
            val queryTime = System.currentTimeMillis() - startTime

            connection.close()

            MySqlQueryResult(
                success = true,
                data = result.first,
                columnNames = result.second,
                rowCount = result.first.size,
                queryTime = queryTime
            )
        } catch (e: Exception) {
            MySqlQueryResult(
                success = false,
                errorMessage = e.message
            )
        }
    }

    /**
     * 执行参数化更新
     */
    fun executeUpdateWithParams(sql: String, params: List<Any?>): MySqlQueryResult {
        return try {
            val connection = getConnection() ?: throw SQLException("无法获取数据库连接")

            val startTime = System.currentTimeMillis()
            val statement = prepareStatementWithParams(connection, sql, params)
            val affectedRows = statement.executeUpdate()
            val queryTime = System.currentTimeMillis() - startTime

            connection.close()

            MySqlQueryResult(
                success = true,
                affectedRows = affectedRows,
                queryTime = queryTime
            )
        } catch (e: Exception) {
            MySqlQueryResult(
                success = false,
                errorMessage = e.message
            )
        }
    }

    /**
     * 处理ResultSet为所需的数据格式
     */
    private fun processResultSet(resultSet: ResultSet): Pair<List<Map<String, Any?>>, List<String>> {
        val metaData = resultSet.metaData
        val columnCount = metaData.columnCount

        // 获取列名
        val columnNames = mutableListOf<String>()
        for (i in 1..columnCount) {
            // 尝试获取列标签，如果获取不到则使用列名
            val columnName = try {
                metaData.getColumnLabel(i)
            } catch (e: Exception) {
                metaData.getColumnName(i)
            }
            columnNames.add(columnName)
        }

        // 获取数据
        val rows = mutableListOf<Map<String, Any?>>()
        while (resultSet.next()) {
            val row = mutableMapOf<String, Any?>()
            for (i in 1..columnCount) {
                val columnName = try {
                    metaData.getColumnLabel(i)
                } catch (_: Exception) {
                    metaData.getColumnName(i)
                }
                val value = try {
                    resultSet.getObject(i)
                } catch (e: Exception) {
                    "ERROR: ${e.message}"
                }
                row[columnName] = value
            }
            rows.add(row)
        }

        return Pair(rows.toList(), columnNames.toList())
    }

    /**
     * 设置参数到PreparedStatement
     */
    private fun prepareStatementWithParams(connection: Connection, sql: String, params: List<Any?>): PreparedStatement {
        val statement = connection.prepareStatement(sql)
        params.forEachIndexed { index, param ->
            statement.setObject(index + 1, param)
        }
        return statement
    }

    /**
     * 关闭数据库连接池
     */
    fun closeConnectionPool() {
        (dataSource as? HikariDataSource)?.close()
    }
}