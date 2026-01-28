package org.treeroot.devlog.db.mysql

import org.treeroot.devlog.data.MySqlQueryResult

/**
 * MySQL数据库服务类
 * 提供高级数据库操作接口
 */
class MySqlDatabaseService {
    private val databaseManager = MySqlDatabaseManager()

    /**
     * 初始化数据库连接
     */
    fun initializeConnection(
        host: String,
        port: Int = 3306,
        database: String,
        username: String,
        password: String,
        poolSize: Int = 10
    ) {
        val config = MySqlConfig(
            host = host,
            port = port,
            database = database,
            username = username,
            password = password,
            poolSize = poolSize
        )
        databaseManager.initializeConnectionPool(config)
    }

    /**
     * 使用配置对象初始化数据库连接
     */
    fun initializeConnectionWithConfig(config: MySqlConfig) {
        databaseManager.initializeConnectionPool(config)
    }

    /**
     * 执行查询操作
     */
    suspend fun query(sql: String): MySqlQueryResult {
        return databaseManager.executeQuery(sql)
    }

    /**
     * 执行更新操作
     */
    suspend fun update(sql: String): MySqlQueryResult {
        return databaseManager.executeUpdate(sql)
    }

    /**
     * 执行带参数的查询
     */
    suspend fun queryWithParams(sql: String, params: List<Any?>): MySqlQueryResult {
        return databaseManager.executeQueryWithParams(sql, params)
    }

    /**
     * 执行带参数的更新
     */
    suspend fun updateWithParams(sql: String, params: List<Any?>): MySqlQueryResult {
        return databaseManager.executeUpdateWithParams(sql, params)
    }

    /**
     * 测试数据库连接
     */
    suspend fun testConnection(): Boolean {
        return try {
            val result = databaseManager.executeQuery("SELECT 1 AS test")
            result.success
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 关闭数据库连接
     */
    fun closeConnection() {
        databaseManager.closeConnectionPool()
    }
}