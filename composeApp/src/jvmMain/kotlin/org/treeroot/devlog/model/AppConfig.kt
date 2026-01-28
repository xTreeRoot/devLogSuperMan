package org.treeroot.devlog.model

/**
 * 应用配置数据类
 */
data class AppConfig(
    val id: Long = 1,
    val backgroundImagePath: String = "",
    val backgroundOpacity: Float = 1.0f,
    val enableClipboardMonitor: Boolean = false
)