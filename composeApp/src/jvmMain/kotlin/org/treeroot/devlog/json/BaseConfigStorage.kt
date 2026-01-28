package org.treeroot.devlog.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * 通用配置存储基类
 * 提供基础的JSON文件读写功能
 */
abstract class BaseConfigStorage<T>(private val configFileName: String) {
    protected open val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    protected val configFile: File by lazy {
        val osName = System.getProperty("os.name").lowercase()
        val configPath = if (osName.contains("win")) {
            // Windows: 使用 AppData 目录
            val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
            "$appData\\DevLog_SuperMan\\$configFileName"
        } else if (osName.contains("mac")) {
            // macOS: 使用 ~/Library/Application Support/
            "${System.getProperty("user.home")}/Library/Application Support/DevLog_SuperMan/$configFileName"
        } else {
            // Linux 和其他 Unix 系统: 使用 ~/.local/share/
            val localShare = System.getProperty("user.home") + "/.local/share"
            "$localShare/DevLog_SuperMan/$configFileName"
        }

        val file = File(configPath)
        file.parentFile?.let { parentDir ->
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
        }
        file
    }

    /**
     * 保存配置到文件
     */
    fun saveConfig(config: T) {
        try {
            // 创建临时文件
            val tempFile = File(configFile.absolutePath + ".tmp")

            // 写入临时文件
            tempFile.writeText(gson.toJson(config))

            // 原子性地移动临时文件到目标文件
            Files.move(tempFile.toPath(), configFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println("Failed to save config: ${e.message}")
        }
    }


    /**
     * 从文件加载配置
     */
    fun loadConfig(defaultConfig: T): T {
        return try {
            if (configFile.exists() && configFile.length() > 0) {
                val jsonContent = configFile.readText()
                val config = gson.fromJson(jsonContent, getConfigClass())

                // 如果解析失败，返回默认配置
                config ?: defaultConfig
            } else {
                // 如果文件不存在，返回默认配置
                defaultConfig
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println("Failed to load config: ${e.message}")
            // 返回默认配置
            defaultConfig
        }
    }

    /**
     * 获取配置类的Class对象
     */
    protected abstract fun getConfigClass(): Class<T>

    /**
     * 获取配置文件是否存在
     */
    fun configFileExists(): Boolean {
        return configFile.exists()
    }
}