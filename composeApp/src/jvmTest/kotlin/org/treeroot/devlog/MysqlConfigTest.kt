package org.treeroot.devlog

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.treeroot.devlog.json.model.MySqlConfig
import org.treeroot.devlog.logic.MySqlDatabaseService
import org.treeroot.devlog.service.JsonStoreService
import java.util.*

/**
 * MySQL配置存储功能测试类
 */
class MysqlConfigTest {

    @Test
    fun testMysqlConfigStorage() = runBlocking {
        println("开始测试MySQL配置存储功能...")
        JsonStoreService.deleteAllMysqlConfigs()

        // 1. 测试添加MySQL配置
        val configId = UUID.randomUUID().toString()
        val mysqlConfig = MySqlConfig(
            id = configId,
            name = "测试数据库配置",
            host = "localhost",
            port = 3306,
            database = "information_schema",
            username = "root",
            password = "password",
            isDefault = false,
            remarks = "本地测试数据库"
        )

        println("添加MySQL配置...")
        JsonStoreService.addMySqlConfig(mysqlConfig)
        println("MySQL配置添加成功")

        // 2. 测试获取所有MySQL配置
        println("获取所有MySQL配置...")
        val allConfigs = JsonStoreService.getAllMySqlConfigs()
        println("共有 ${allConfigs.size} 个MySQL配置")
        allConfigs.forEach { config ->
            println("配置: ${config.name} (${config.host}:${config.port})")
        }

        // 3. 测试获取特定配置
        println("获取特定MySQL配置...")
        val retrievedConfig = JsonStoreService.getMySqlConfigById(configId)
        println("检索到配置: ${retrievedConfig?.name}")

        // 4. 测试更新配置
        println("更新MySQL配置...")
        val updatedConfig = mysqlConfig.copy(name = "更新后的测试数据库配置", remarks = "已更新的配置", isDefault = true)
        JsonStoreService.updateMySqlConfig(updatedConfig)
        println("MySQL配置更新成功")

        // 5. 验证更新结果
        val updatedRetrievedConfig = JsonStoreService.getMySqlConfigById(configId)
        println("更新后的配置名称: ${updatedRetrievedConfig?.name}")

        // 6. 测试设置默认配置
        println("设置默认MySQL配置...")
        JsonStoreService.setDefaultMySqlConfig(configId)
        println("默认配置设置成功")

        // 7. 验证默认配置
        val allConfigsAfterSetDefault = JsonStoreService.getAllMySqlConfigs()
        val defaultConfig = allConfigsAfterSetDefault.find { it.isDefault }
        println("默认配置: ${defaultConfig?.name}")

        // 8. 测试连接功能
        println("测试MySQL配置连接...")
        val databaseService = MySqlDatabaseService()
        val connectionResult = databaseService.testConnectionWithConfig(updatedRetrievedConfig!!)
        println("连接测试结果: ${if (connectionResult) "成功" else "失败"}")

        println("MySQL配置存储功能测试完成")
    }
}