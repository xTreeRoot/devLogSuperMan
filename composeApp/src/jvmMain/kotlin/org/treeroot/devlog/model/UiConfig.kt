package org.treeroot.devlog.model

/**
 * UI配置数据类
 * 用于存储用户界面相关配置
 */
data class UiConfig(
    val id: Long = 1,
    /**
     * 背景图片路径
     */
    val backgroundImagePath: String = "",
    /**
     * 背景图片透明度
     */
    val backgroundOpacity: Float = 1.0f,
    /**
     * 是否启用剪贴板监控
     */
    val enableClipboardMonitor: Boolean = false
)