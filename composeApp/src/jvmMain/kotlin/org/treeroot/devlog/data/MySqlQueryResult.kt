package org.treeroot.devlog.data

/**
 * MySQL查询结果数据类
 * 包含查询结果和相关信息
 */
data class MySqlQueryResult(
    val success: Boolean,
    val data: List<Map<String, Any?>> = emptyList(),
    val columnNames: List<String> = emptyList(),
    val rowCount: Int = 0,
    val queryTime: Long = System.currentTimeMillis(),
    val errorMessage: String? = null,
    val affectedRows: Int = 0
)