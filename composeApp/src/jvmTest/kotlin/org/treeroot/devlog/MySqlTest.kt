package org.treeroot.devlog

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.treeroot.devlog.config.ConfigManager
import org.treeroot.devlog.data.MySqlQueryResult
import org.treeroot.devlog.db.mysql.MySqlConfig
import org.treeroot.devlog.db.mysql.MySqlDatabaseService

/**
 * MySQL数据库操作示例
 */
class MySqlTest {
    private val databaseService = MySqlDatabaseService()

    /**
     * 初始化并演示数据库操作
     */
    @Test
     fun demonstrateDatabaseOperations() = runBlocking {
        // 从配置文件加载MySQL测试配置
        val configManager = ConfigManager.instance
        configManager.loadTestConfig("test-config.yaml")
        val mysqlTestConfig = configManager.getMySqlTestConfig()

        // 使用配置文件中的配置创建MySQL配置
        val config = MySqlConfig(
            host = mysqlTestConfig.host,
            port = mysqlTestConfig.port,
            database = mysqlTestConfig.database,
            username = mysqlTestConfig.username,
            password = mysqlTestConfig.password,
            poolSize = mysqlTestConfig.poolSize
        )
        // 初始化连接
        databaseService.initializeConnectionWithConfig(config)

        // 测试连接
        val isConnected = databaseService.testConnection()
        println("数据库连接状态: $isConnected")

        if (isConnected) {
            // 执行查询示例
            val queryResult = databaseService.query("SELECT * FROM t_category LIMIT 10")
            handleQueryResult(queryResult)

            // 执行带参数的查询
            val paramQueryResult = databaseService.queryWithParams(
                "SELECT * FROM t_category WHERE level > ? AND deleted = ?",
                listOf(1, 0)
            )
            handleQueryResult(paramQueryResult)
        }

        // 关闭连接
        databaseService.closeConnection()
    }

    /**
     * 处理查询结果
     */
     fun handleQueryResult(result: MySqlQueryResult) {
        if (result.success) {
            println("查询成功！返回 ${result.rowCount} 行数据")
            println("列名: ${result.columnNames}")
            result.data.forEach { row ->
                println("数据行: $row")
            }
        } else {
            println("查询失败: ${result.errorMessage}")
        }
    }


}