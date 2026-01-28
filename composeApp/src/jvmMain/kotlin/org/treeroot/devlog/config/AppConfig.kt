package org.treeroot.devlog.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.treeroot.devlog.model.UiConfig
import java.io.File

/**
 * 应用配置类
 * 用于加载和管理应用配置
 */
data class AppConfig(
    val ai: AiConfig = AiConfig(),
    val app: AppInfo = AppInfo(),
    val database: DatabaseConfig = DatabaseConfig(),
    val logging: LoggingConfig = LoggingConfig(),
    val ui: UiConfig = UiConfig(),
    val mysqlTest: MySqlTestConfig = MySqlTestConfig()
)

/**
 * AI配置类
 */
data class AiConfig(
    val zhipu: ZhipuConfig = ZhipuConfig(),
    val openai: OpenAiConfig = OpenAiConfig(),
    val qwen: QwenConfig = QwenConfig()
)

/**
 * 应用信息类
 */
data class AppInfo(
    val name: String = "DevLogSuperMan",
    val version: String = "1.0.0",
    val debug: Boolean = true
)


data class LoggingConfig(
    val level: String = "INFO"
)

/**
 * 配置管理器
 * 负责加载和提供应用配置
 */
class ConfigManager {
    private val objectMapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule.Builder().build())
    private var config: AppConfig? = null

    companion object {
        @JvmStatic
        val instance: ConfigManager by lazy { ConfigManager() }
    }

    /**
     * 加载配置文件
     */
    fun loadConfig(configPath: String = "config.yaml"): AppConfig {
        // 首先尝试在项目根目录查找配置文件
        val configFile = File(configPath)

        if (configFile.exists()) {
            try {
                config = objectMapper.readValue(configFile, AppConfig::class.java)
                println("成功从文件加载配置: ${configFile.absolutePath}")
                return config!!
            } catch (e: Exception) {
                println("从文件加载配置失败: ${e.message}")
                return AppConfig()
            }
        } else {
            // 尝试在当前工作目录的父目录查找（适用于从IDE运行的情况）
            val parentDirConfigFile = File("../$configPath")
            if (parentDirConfigFile.exists()) {
                try {
                    config = objectMapper.readValue(parentDirConfigFile, AppConfig::class.java)
                    println("成功从父目录加载配置: ${parentDirConfigFile.absolutePath}")
                    return config!!
                } catch (e: Exception) {
                    println("从父目录加载配置失败: ${e.message}")
                    return AppConfig()
                }
            }
        }

        // 如果配置文件不存在，返回默认配置
        println("配置文件 $configPath 不存在，使用默认配置")
        return AppConfig()
    }

    /**
     * 加载测试配置文件
     */
    fun loadTestConfig(configPath: String = "test-config.yaml"): AppConfig {
        // 首先尝试在项目根目录查找配置文件
        val configFile = File(configPath)

        if (configFile.exists()) {
            try {
                config = objectMapper.readValue(configFile, AppConfig::class.java)
                println("成功从文件加载测试配置: ${configFile.absolutePath}")
                return config!!
            } catch (e: Exception) {
                println("从文件加载测试配置失败: ${e.message}")
                return AppConfig()
            }
        } else {
            // 尝试在当前工作目录的父目录查找（适用于从IDE运行的情况）
            val parentDirConfigFile = File("../$configPath")
            if (parentDirConfigFile.exists()) {
                try {
                    config = objectMapper.readValue(parentDirConfigFile, AppConfig::class.java)
                    println("成功从父目录加载测试配置: ${parentDirConfigFile.absolutePath}")
                    return config!!
                } catch (e: Exception) {
                    println("从父目录加载配置失败: ${e.message}")
                    return AppConfig()
                }
            }
        }

        // 如果配置文件不存在，返回默认配置
        println("测试配置文件 $configPath 不存在，使用默认配置")
        return AppConfig()
    }

    /**
     * 获取配置
     */
    fun getConfig(): AppConfig {
        return config ?: loadConfig()
    }

    /**
     * 获取AI配置
     */
    fun getAiConfig(): AiConfig {
        return getConfig().ai
    }

    /**
     * 获取智谱AI配置
     */
    fun getZhipuConfig(): ZhipuConfig {
        return getAiConfig().zhipu
    }

    /**
     * 获取MySQL测试配置
     */
    fun getMySqlTestConfig(): MySqlTestConfig {
        return getConfig().mysqlTest
    }
}