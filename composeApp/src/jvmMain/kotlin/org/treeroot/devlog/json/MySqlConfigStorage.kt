package org.treeroot.devlog.json

import org.treeroot.devlog.mysql.MySqlConfigInfo

/**
 * MySQL配置存储类
 * 专门用于存储MySQL连接配置列表
 */
class MySqlConfigStorage : ListConfigStorage<MySqlConfigInfo>("mysql_configs.json") {
    
    /**
     * 加载MySQL配置列表
     */
    fun loadMySqlConfigs(): List<MySqlConfigInfo> {
        return loadConfig()
    }
    
    /**
     * 添加单个MySQL配置
     */
    fun addMySqlConfig(config: MySqlConfigInfo): List<MySqlConfigInfo> {
        val configs = loadMySqlConfigs().toMutableList()
        // 检查ID是否已存在
        if (configs.any { it.id == config.id }) {
            throw IllegalArgumentException("MySQL配置ID ${config.id} 已存在")
        }
        configs.add(config)
        saveConfig(configs)
        return configs
    }
    
    /**
     * 更新单个MySQL配置
     */
    fun updateMySqlConfig(config: MySqlConfigInfo): List<MySqlConfigInfo> {
        val configs = loadMySqlConfigs().map { existingConfig ->
            if (existingConfig.id == config.id) {
                config
            } else {
                existingConfig
            }
        }.toMutableList()
        
        // 如果配置不存在，则添加新配置
        if (!configs.any { it.id == config.id }) {
            configs.add(config)
        }
        
        saveConfig(configs)
        return configs
    }
    
    /**
     * 删除MySQL配置
     */
    fun deleteMySqlConfig(id: String): List<MySqlConfigInfo> {
        val configs = loadMySqlConfigs().filter { it.id != id }.toMutableList()
        saveConfig(configs)
        return configs
    }
    
    /**
     * 根据ID获取MySQL配置
     */
    fun getMySqlConfigById(id: String): MySqlConfigInfo? {
        val configs = loadMySqlConfigs()
        return configs.find { it.id == id }
    }
    
    /**
     * 设置默认MySQL配置
     */
    fun setDefaultMySqlConfig(id: String): List<MySqlConfigInfo> {
        val configs = loadMySqlConfigs().map { existingConfig ->
            if (existingConfig.id == id) {
                existingConfig.copy(isDefault = true)
            } else {
                existingConfig.copy(isDefault = false)
            }
        }
        saveConfig(configs)
        return configs
    }
    
    /**
     * 检查MySQL配置文件是否存在
     */
    fun mysqlConfigFileExists(): Boolean {
        return configFileExists()
    }
}