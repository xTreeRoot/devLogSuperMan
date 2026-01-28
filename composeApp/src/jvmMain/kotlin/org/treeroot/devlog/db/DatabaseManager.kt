package org.treeroot.devlog.db

import java.io.File

class DatabaseManager {
    private val databasePath = getDatabasePath()
    
    init {
        // 确保目录存在
        val databaseFile = File(databasePath)
        databaseFile.parentFile?.let { parentDir ->
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
        }
    }
    
    fun getDatabasePath(): String {
        val osName = System.getProperty("os.name").lowercase()
        
        return if (osName.contains("win")) {
            // Windows: 使用 AppData 目录
            val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
            "$appData\\DevLog_SuperMan\\app_database.db"
        } else if (osName.contains("mac")) {
            // macOS: 使用 ~/Library/Application Support/
            "${System.getProperty("user.home")}/Library/Application Support/DevLog_SuperMan/app_database.db"
        } else {
            // Linux 和其他 Unix 系统: 使用 ~/.local/share/
            val localShare = System.getProperty("user.home") + "/.local/share"
            "$localShare/DevLog_SuperMan/app_database.db"
        }
    }
    
    fun getDatabasePathString(): String {
        return databasePath
    }
}