package org.treeroot.devlog.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.treeroot.devlog.db.AppConfigStorage
import org.treeroot.devlog.model.UiConfig
import org.treeroot.devlog.state.AppStateManager

class DatabaseService {
    private val configStorage = AppConfigStorage()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    fun loadConfig(): UiConfig {
        return configStorage.loadConfig()
    }

    suspend fun saveConfig(config: UiConfig) {
        configStorage.saveConfig(config)
    }

    fun saveConfigAsync(config: UiConfig, onComplete: (() -> Unit)? = null) {
        ioScope.launch {
            configStorage.saveConfig(config)
            AppStateManager.updateConfig(config)
            onComplete?.invoke()
        }
    }

    suspend fun configExists(): Boolean {
        // 对于文件存储，我们认为配置总是存在的（即使为空）
        return true
    }
}

