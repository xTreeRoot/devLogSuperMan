package org.treeroot.devlog.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.treeroot.devlog.db.AppConfigDao
import org.treeroot.devlog.db.AppConfigRepository
import org.treeroot.devlog.db.DatabaseManager
import org.treeroot.devlog.model.UiConfig
import org.treeroot.devlog.state.AppStateManager

class DatabaseService {
    private val databaseManager = DatabaseManager()
    private var dao: AppConfigDao
    private var repository: AppConfigRepository

    init {
        var dbPath = databaseManager.getDatabasePathString()
        var attempts = 0
        val maxAttempts = 3
        
        while (attempts < maxAttempts) {
            try {
                this.dao = AppConfigDao(dbPath)
                this.repository = AppConfigRepository(dao)
                break // 成功初始化，退出循环
            } catch (e: Exception) {
                attempts++
                e.printStackTrace()
                if (attempts >= maxAttempts) {
                    // 如果多次尝试都失败，使用临时路径
                    System.err.println("All attempts to initialize database failed, using temporary path")
                    val tempDbPath = System.getProperty("java.io.tmpdir") + "/devlog_temp.db"
                    this.dao = AppConfigDao(tempDbPath)
                    this.repository = AppConfigRepository(dao)
                } else {
                    // 递增延迟重试
                    Thread.sleep((100 * attempts).toLong())
                    // 尝试重新获取数据库路径（可能已切换到临时路径）
                    dbPath = databaseManager.getDatabasePathString()
                }
            }
        }
    }
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun loadConfig(): UiConfig {
        var config: UiConfig? = null

        // 在同步代码中调用挂起函数，使用 runBlocking
        runBlocking {
            config = repository.getAppConfig()
        }

        return config ?: UiConfig()
    }

    suspend fun saveConfig(config: UiConfig) {
        repository.saveAppConfig(config)
    }

    fun saveConfigAsync(config: UiConfig, onComplete: (() -> Unit)? = null) {
        ioScope.launch {
            repository.saveAppConfig(config)
            AppStateManager.updateConfig(config)
            onComplete?.invoke()
        }
    }

    suspend fun configExists(): Boolean {
        return repository.configExists()
    }
}

