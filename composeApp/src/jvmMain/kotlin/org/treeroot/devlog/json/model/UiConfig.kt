package org.treeroot.devlog.json.model

/**
 * UI配置数据类
 * 用于存储用户界面相关配置
 */
data class UiConfig(
    /**
     * 固定ID
     */
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
    val enableClipboardMonitor: Boolean = false,
    /**
     * 是否系统自适应主题
     */
    val isSystemAdaptive: Boolean = true,
    /**
     * 主题模式：false为亮色，true为暗色
     * 当isSystemAdaptive为true时此设置无效
     */
    val isDarkTheme: Boolean = false
)