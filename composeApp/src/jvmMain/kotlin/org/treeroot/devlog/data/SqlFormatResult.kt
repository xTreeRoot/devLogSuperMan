package org.treeroot.devlog.data

/**
 * SQL格式化结果数据类
 * 包含格式化后的SQL和相关信息
 */
data class SqlFormatResult(
    val originalSql: String,
    val formattedSql: String,
    val isValid: Boolean,
    val errorMessage: String? = null,
    val formatTime: Long = System.currentTimeMillis()
)