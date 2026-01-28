package org.treeroot.devlog.db

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.treeroot.devlog.model.UiConfig
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * 使用纯 Kotlin 方式存储应用配置，避免 JDBC 依赖
 */
class AppConfigStorage {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File

    init {
        val osName = System.getProperty("os.name").lowercase()
        val configPath = if (osName.contains("win")) {
            // Windows: 使用 AppData 目录
            val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
            "$appData\\DevLog_SuperMan\\config.json"
        } else if (osName.contains("mac")) {
            // macOS: 使用 ~/Library/Application Support/
            "${System.getProperty("user.home")}/Library/Application Support/DevLog_SuperMan/config.json"
        } else {
            // Linux 和其他 Unix 系统: 使用 ~/.local/share/
            val localShare = System.getProperty("user.home") + "/.local/share"
            "$localShare/DevLog_SuperMan/config.json"
        }

        configFile = File(configPath)
        configFile.parentFile?.let { parentDir ->
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
        }
    }

    /**
     * 保存配置到文件
     */
    fun saveConfig(config: UiConfig) {
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
    fun loadConfig(): UiConfig {
        return try {
            if (configFile.exists() && configFile.length() > 0) {
                val jsonContent = configFile.readText()
                val config = gson.fromJson(jsonContent, UiConfig::class.java)
                
                // 如果解析失败，返回默认配置
                config ?: UiConfig()
            } else {
                // 如果文件不存在，返回默认配置
                UiConfig()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            System.err.println("Failed to load config: ${e.message}")
            // 返回默认配置
            UiConfig()
        }
    }
}