package org.treeroot.devlog.json.model

/**
 * MySQL配置信息数据类
 * 用于存储MySQL连接配置信息，包括连接参数、备注和默认设置
 */
data class MySqlConfig(
    /**
     * 配置唯一标识
     */
    val id: String = "",
    /**
     * 配置名称或备注
     */
    val name: String = "",
    /**
     * 主机地址
     */
    val host: String = "localhost",
    /**
     * 端口号
     */
    val port: Int = 3306,
    /**
     * 数据库名称
     */
    val database: String = "",
    /**
     * 用户名
     */
    val username: String = "",
    /**
     * 密码
     */
    val password: String = "",
    /**
     * 是否为默认配置
     */
    val isDefault: Boolean = false,
    /**
     * 备注信息
     */
    val remarks: String = ""
)