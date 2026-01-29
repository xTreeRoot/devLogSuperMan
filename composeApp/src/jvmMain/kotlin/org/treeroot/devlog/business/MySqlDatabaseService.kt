package org.treeroot.devlog.business

import org.treeroot.devlog.business.model.MySqlQueryResult
import org.treeroot.devlog.mysql.MySqlConnectConfig
import org.treeroot.devlog.json.model.MySqlConfig
import org.treeroot.devlog.mysql.MySqlDatabaseManager
import org.treeroot.devlog.service.JsonStoreService
import java.sql.DriverManager
import java.util.Properties

/**
 * MySQL数据库服务类
 * 提供高级数据库操作接口
 */
class MySqlDatabaseService {
    private val databaseManager = MySqlDatabaseManager()

    // 记录当前激活的配置ID
    private var activeConfigId: String? = null

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
        val config = MySqlConnectConfig(
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
    fun initializeConnectionWithConfig(config: MySqlConnectConfig) {
        databaseManager.initializeConnectionPool(config)
    }

    /**
     * 根据配置ID激活数据库连接
     */
    fun activateConnectionWithConfigId(configId: String): Boolean {
        val config = JsonStoreService.getMySqlConfigById(configId)
        if (config != null) {
            val connectConfig = MySqlConnectConfig(
                host = config.host,
                port = config.port,
                database = config.database,
                username = config.username,
                password = config.password
            )
            databaseManager.initializeConnectionPool(connectConfig)
            activeConfigId = configId
            return true
        }
        return false
    }

    /**
     * 获取当前激活的配置ID
     */
    fun getActiveConfigId(): String? {
        return activeConfigId
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
     * 使用MySQL配置信息测试数据库连接
     */
    suspend fun testConnectionWithConfig(configInfo: MySqlConfig): Boolean {
        return try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            val url = "jdbc:mysql://${configInfo.host}:${configInfo.port}/${configInfo.database}?connectTimeout=5000&socketTimeout=10000"
            val props = Properties()
            props.setProperty("user", configInfo.username)
            props.setProperty("password", configInfo.password)
            props.setProperty("useSSL", "false")
            props.setProperty("allowPublicKeyRetrieval", "true")

            DriverManager.getConnection(url, props).use { connection ->
                connection.isValid(5) // 5秒超时
            }
        } catch (e: Exception) {
            e.printStackTrace()
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