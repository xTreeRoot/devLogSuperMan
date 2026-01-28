package org.treeroot.devlog.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.treeroot.devlog.json.MySqlConfigStorage
import org.treeroot.devlog.json.UiConfigStorage
import org.treeroot.devlog.model.UiConfig
import org.treeroot.devlog.mysql.MySqlConfigInfo
import org.treeroot.devlog.state.AppStateManager

class JsonStoreService {
    private val uiConfigStorage = UiConfigStorage()
    private val mySqlConfigStorage = MySqlConfigStorage()
    private val ioScope = CoroutineScope(Dispatchers.IO)

    /**
     * 加载UI配置
     */
    fun loadUiConfig(): UiConfig {
        return uiConfigStorage.loadUiConfig()
    }

    /**
     * 异步保存UI配置
     */
    fun saveConfigAsync(config: UiConfig, onComplete: (() -> Unit)? = null) {
        ioScope.launch {
            uiConfigStorage.saveUiConfig(config)
            AppStateManager.updateConfig(config)
            onComplete?.invoke()
        }
    }

    /**
     * 获取所有MySQL配置
     */
    fun getAllMySqlConfigs(): List<MySqlConfigInfo> {
        return mySqlConfigStorage.loadMySqlConfigs()
    }

    /**
     * 获取特定MySQL配置详情
     */
    fun getMySqlConfigById(id: String): MySqlConfigInfo? {
        return mySqlConfigStorage.getMySqlConfigById(id)
    }

    /**
     * 更新MySQL配置
     */
    fun updateMySqlConfig(config: MySqlConfigInfo) {
        mySqlConfigStorage.updateMySqlConfig(config)
    }

    /**
     * 添加新的MySQL配置
     */
    fun addMySqlConfig(config: MySqlConfigInfo) {
        mySqlConfigStorage.addMySqlConfig(config)
    }

    /**
     * 删除MySQL配置
     */
    fun deleteMySqlConfig(id: String) {
        mySqlConfigStorage.deleteMySqlConfig(id)
    }
    fun deleteAllMysqlConfigs() {
        mySqlConfigStorage.deleteAllMysqlConfigs()
    }

    /**
     * 设置默认MySQL配置
     */
    fun setDefaultMySqlConfig(id: String) {
        mySqlConfigStorage.setDefaultMySqlConfig(id)
    }
}

