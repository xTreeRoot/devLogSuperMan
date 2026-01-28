package org.treeroot.devlog.config

data class DatabaseConfig(
    val url: String = "",
    val username: String = "",
    val password: String = ""
)

data class MySqlTestConfig(
    val host: String = "localhost",
    val port: Int = 3306,
    val database: String = "",
    val username: String = "",
    val password: String = "",
    val poolSize: Int = 10,
    val useSSL: Boolean = false,
    val connectTimeout: Int = 30000,
    val socketTimeout: Int = 60000
)
