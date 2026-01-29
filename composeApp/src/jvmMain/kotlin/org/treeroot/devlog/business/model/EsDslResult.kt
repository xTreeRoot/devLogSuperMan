package org.treeroot.devlog.business.model

/**
 * ES DSL处理结果数据类
 * 包含ES DSL处理结果和相关信息
 */
data class EsDslResult(
    val success: Boolean,
    val formattedDsl: String = "",
    val formattedResponse: String = "",
    val processingTime: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)