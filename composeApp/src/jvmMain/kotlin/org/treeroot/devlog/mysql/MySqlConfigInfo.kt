package org.treeroot.devlog.mysql

/**
 * MySQL配置信息数据类
 * 用于存储MySQL连接配置信息，包括连接参数、备注和默认设置
 */
data class MySqlConfigInfo(
    val id: String = "",                    // 配置唯一标识
    val name: String = "",                  // 配置名称或备注
    val host: String = "localhost",         // 主机地址
    val port: Int = 3306,                   // 端口号
    val database: String = "",              // 数据库名称
    val username: String = "",              // 用户名
    val password: String = "",              // 密码
    val isDefault: Boolean = false,         // 是否为默认配置
    val remarks: String = ""                // 备注信息
)