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
    private val dao = AppConfigDao(databaseManager.getDatabasePathString())
    private val repository = AppConfigRepository(dao)

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

