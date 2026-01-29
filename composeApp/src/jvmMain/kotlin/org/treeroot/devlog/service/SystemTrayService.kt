package org.treeroot.devlog.service

import org.treeroot.devlog.DevLog
import org.treeroot.devlog.ui.TrayPopup
import org.treeroot.devlog.util.ImageUtils
import java.awt.*
import javax.imageio.ImageIO

/**
 * 系统托盘服务
 * 用于在系统托盘中显示应用图标并提供快捷菜单
 */
object SystemTrayService {
    private var trayIcon: TrayIcon? = null
    private var systemTray: SystemTray? = null

    /**
     * 初始化系统托盘
     */
    fun initializeTray() {
        if (!SystemTray.isSupported()) {
            DevLog.warn("系统托盘不受支持")
            return
        }
        systemTray = SystemTray.getSystemTray()
        var trayIconImage: Image?
        try {
            trayIconImage = ImageUtils.loadImage("org/treeroot/devlog/ATM.png", createStopTrayIcon())
        } catch (e: Exception) {
            trayIconImage = createStopTrayIcon()
            DevLog.warn("无法加载指定图标，使用默认图标: ${e.message}")
        }
        trayIcon = TrayIcon(trayIconImage, "devLogSuperMan")
        trayIcon?.isImageAutoSize = true
        // 添加鼠标事件监听器来显示菜单
        trayIcon?.addActionListener { TrayPopup.showMainWindow() }
        // 创建弹出菜单
        val popupMenu = TrayPopup.createPopupMenu()
        trayIcon?.popupMenu = popupMenu
        try {
            systemTray?.add(trayIcon)
            DevLog.info("系统托盘图标已添加")
        } catch (e: AWTException) {
            DevLog.error("无法添加系统托盘图标: ${e.message}")
        }
    }

    /**
     * 更新托盘图标以反映监控状态
     */
    fun updateTrayIconBasedOnStatus() {
        val isMonitoring = ClipboardMonitorService.isMonitoring()

        // 获取对应状态的图标
        val trayIconImage = if (isMonitoring) {
            ImageIO.read(ImageUtils.loadImageInputStream("org/treeroot/devlog/ATM.png"))
        } else {
            // 停止时使用红色空心方块
            createStopTrayIcon()
        }

        // 更新托盘图标
        trayIcon?.image = trayIconImage

        // 更新工具提示文本以显示当前监控状态
        val tooltip = if (isMonitoring) {
            "devLogSuperMan - 监控中"
        } else {
            "devLogSuperMan - 已停止"
        }
        trayIcon?.toolTip = tooltip
    }

    /**
     * 创建默认托盘图标（监控状态：停止）
     */
    private fun createStopTrayIcon(): Image {
        return TrayPopup.createTrayIcon("\u25A1", Color.RED)
    }

    /**
     * 创建默认托盘图标（监控状态：停止）
     */
    @Suppress("unused")
    private fun createStartTrayIcon(): Image {
        return TrayPopup.createTrayIcon("Start", Color.GREEN)
    }


}