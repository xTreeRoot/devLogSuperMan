package org.treeroot.devlog.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.treeroot.devlog.db.AppConfigDao
import org.treeroot.devlog.db.AppConfigRepository
import org.treeroot.devlog.db.DatabaseManager
import org.treeroot.devlog.model.AppConfig

class DatabaseService {
    private val databaseManager = DatabaseManager()
    private val dao = AppConfigDao(databaseManager.getDatabasePathString())
    private val repository = AppConfigRepository(dao)

    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun loadConfig(): AppConfig {
        var config: AppConfig? = null

        // 在同步代码中调用挂起函数，使用 runBlocking
        runBlocking {
            config = repository.getAppConfig()
        }

        return config ?: AppConfig()
    }

    suspend fun saveConfig(config: AppConfig) {
        repository.saveAppConfig(config)
    }

    fun saveConfigAsync(config: AppConfig, onComplete: (() -> Unit)? = null) {
        ioScope.launch {
            repository.saveAppConfig(config)
            onComplete?.invoke()
        }
    }

    suspend fun configExists(): Boolean {
        return repository.configExists()
    }
}

