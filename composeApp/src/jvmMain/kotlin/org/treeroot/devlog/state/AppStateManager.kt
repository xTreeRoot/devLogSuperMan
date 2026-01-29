package org.treeroot.devlog.state

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.treeroot.devlog.json.model.UiConfig

/**
 * 应用状态管理器，用于在组件之间共享配置状态
 */
object AppStateManager {
    private val _configUpdates = MutableSharedFlow<UiConfig>(replay = 1)
    val configUpdates = _configUpdates.asSharedFlow()

    private var _currentConfig = mutableStateOf<UiConfig?>(null)
    val currentConfig get() = _currentConfig.value

    /**
     * 更新配置并通知所有订阅者
     */
    fun updateConfig(newConfig: UiConfig) {
        _currentConfig.value = newConfig
        _configUpdates.tryEmit(newConfig)
    }

    /**
     * 发送配置更新
     */
    suspend fun emitConfigUpdate(newConfig: UiConfig) {
        _currentConfig.value = newConfig
        _configUpdates.emit(newConfig)
    }
}