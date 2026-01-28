package org.treeroot.devlog.db

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.treeroot.devlog.model.UiConfig

class AppConfigRepository(private val dao: AppConfigDao) {

    /**
     * 获取应用配置，如果不存在则返回默认值
     */
    suspend fun getAppConfig(): UiConfig = withContext(Dispatchers.IO) {
        val configQuery = dao.getConfig()

        configQuery
    }

    /**
     * 保存应用配置
     */
    suspend fun saveAppConfig(config: UiConfig) = withContext(Dispatchers.IO) {
        dao.saveConfig(config)
    }

    /**
     * 检查配置是否存在
     */
    suspend fun configExists(): Boolean = withContext(Dispatchers.IO) {
        dao.configExists()
    }
}