package org.treeroot.devlog.db

import java.io.File

class DatabaseManager {
    private var databasePath: String? = null
    
    init {
        // 初始化数据库路径
        this.databasePath = getDatabasePath()
    }
    
    fun getDatabasePath(): String {
        val osName = System.getProperty("os.name").lowercase()
        
        val primaryPath = if (osName.contains("win")) {
            // Windows: 使用 AppData 目录
            val appData = System.getenv("APPDATA") ?: System.getProperty("user.home")
            "$appData\\DevLog_SuperMan\\app_database.db"
        } else if (osName.contains("mac")) {
            // macOS: 首先尝试使用用户目录，这是最安全的选项
            ensureDirectoryExists("${System.getProperty("user.home")}/Library/Application Support/DevLog_SuperMan")
            "${System.getProperty("user.home")}/Library/Application Support/DevLog_SuperMan/app_database.db"
        } else {
            // Linux 和其他 Unix 系统: 使用 ~/.local/share/
            val localShare = System.getProperty("user.home") + "/.local/share"
            "$localShare/DevLog_SuperMan/app_database.db"
        }
        
        // 检查主路径是否可写，如果不可写则使用临时路径
        val primaryFile = File(primaryPath)
        val parentDir = primaryFile.parentFile
        
        if (parentDir != null && !parentDir.exists()) {
            try {
                parentDir.mkdirs()
            } catch (e: SecurityException) {
                System.err.println("Cannot create database directory: ${primaryFile.parent}")
                e.printStackTrace()
                // 如果无法创建目录，使用临时目录
                return getTempDatabasePath()
            }
        }
        
        // 尝试创建一个临时文件来测试写权限
        if (parentDir != null) {
            try {
                val testFile = File(parentDir, ".write_test")
                testFile.createNewFile()
                testFile.delete()
            } catch (e: Exception) {
                System.err.println("No write permission to database directory: ${parentDir.absolutePath}")
                e.printStackTrace()
                // 如果没有写权限，使用临时目录
                return getTempDatabasePath()
            }
        }
        
        return primaryPath
    }
    
    private fun getTempDatabasePath(): String {
        val tempDir = File(System.getProperty("java.io.tmpdir"), "DevLog_SuperMan")
        tempDir.mkdirs()
        val tempDbPath = File(tempDir, "app_database.db").absolutePath
        System.err.println("Using temporary database path: $tempDbPath")
        return tempDbPath
    }
    
    private fun ensureDirectoryExists(path: String) {
        val dir = File(path).parentFile
        if (dir != null && !dir.exists()) {
            try {
                dir.mkdirs()
            } catch (e: SecurityException) {
                System.err.println("Cannot create directory: ${dir.absolutePath}")
            }
        }
    }
    
    fun getDatabasePathString(): String {
        if (databasePath == null) {
            databasePath = getDatabasePath()
        }
        return databasePath!!
    }
}