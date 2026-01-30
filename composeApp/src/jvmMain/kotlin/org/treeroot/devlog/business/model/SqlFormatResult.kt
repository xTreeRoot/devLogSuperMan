package org.treeroot.devlog.business.model

/**
 * SQL格式化结果数据类
 * 包含SQL格式化结果和相关信息
 */
data class SqlFormatResult(
    val success: Boolean,
    val formattedSql: String = "",
    val originalSql: String = "",
    val isValid: Boolean = true,
    val processingTime: Long = System.currentTimeMillis(),
    val errorMessage: String? = null,
    val formatType: String? = null // 格式化类型，如pretty, onenodeperline等
)