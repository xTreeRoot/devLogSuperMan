package org.treeroot.devlog.db.mysql

/**
 * MySQL数据库配置数据类
 */
data class MySqlConfig(
    val host: String = "localhost",
    val port: Int = 3306,
    val database: String,
    val username: String,
    val password: String,
    val poolSize: Int = 10,
    val useSSL: Boolean = false,
    val connectTimeout: Int = 30000,
    val socketTimeout: Int = 60000
)