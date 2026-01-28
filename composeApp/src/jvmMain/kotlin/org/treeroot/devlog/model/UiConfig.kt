package org.treeroot.devlog.model

/**
 * UI配置数据类
 * 用于存储用户界面相关配置
 */
data class UiConfig(
    val id: Long = 1,
    val backgroundImagePath: String = "",
    val backgroundOpacity: Float = 1.0f,
    val enableClipboardMonitor: Boolean = false
)