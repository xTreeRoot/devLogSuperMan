package org.treeroot.devlog.json

import org.treeroot.devlog.model.UiConfig

/**
 * UI配置存储类
 * 专门用于存储UI相关的配置
 */
class UiConfigStorage : BaseConfigStorage<UiConfig>("ui_config.json") {
    
    /**
     * 保存UI配置
     */
    fun saveUiConfig(config: UiConfig) {
        saveConfig(config)
    }
    
    /**
     * 加载UI配置
     */
    fun loadUiConfig(): UiConfig {
        return loadConfig(UiConfig())
    }
    
    /**
     * 检查UI配置文件是否存在
     */
    fun uiConfigFileExists(): Boolean {
        return configFileExists()
    }
    
    override fun getConfigClass(): Class<UiConfig> = UiConfig::class.java
}