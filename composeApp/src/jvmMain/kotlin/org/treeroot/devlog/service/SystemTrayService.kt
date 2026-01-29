package org.treeroot.devlog.service

import org.treeroot.devlog.DevLog
import java.awt.*
import java.awt.image.BufferedImage
import javax.swing.ImageIcon
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

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

        // 获取应用图标
        // 尝试从多个可能的位置加载图标
        val iconStream = javaClass.classLoader.getResourceAsStream("drawable/compose-multiplatform.xml")
            ?: javaClass.classLoader.getResourceAsStream("compose-multiplatform.xml")
            ?: this::class.java.classLoader.getResourceAsStream("org/treeroot/devlog/迪迦.png")

        val trayIconImage = if (iconStream != null) {
            try {
                ImageIcon(javax.imageio.ImageIO.read(iconStream)).image
            } catch (e: Exception) {
                DevLog.warn("无法加载指定图标，使用默认图标: ${e.message}")
                createDefaultTrayIcon()
            }
        } else {
            // 创建一个简单的纯色图标作为备用
            createDefaultTrayIcon()
        }

        trayIcon = TrayIcon(trayIconImage, "devLogSuperMan")
        trayIcon?.isImageAutoSize = true

        // 添加鼠标事件监听器来显示菜单
        trayIcon?.addActionListener { showMainWindow() }

        // 创建弹出菜单
        val popupMenu = createPopupMenu()

        trayIcon?.popupMenu = popupMenu
        try {
            systemTray?.add(trayIcon)
            DevLog.info("系统托盘图标已添加")
        } catch (e: AWTException) {
            DevLog.error("无法添加系统托盘图标: ${e.message}")
        }
    }

    /**
     * 创建弹出菜单
     */
    private fun createPopupMenu(): PopupMenu {
        val popupMenu = PopupMenu()

        // 创建菜单项
        val settingsItem = MenuItem("设置")
        val homeItem = MenuItem("主页")
        val aboutItem = MenuItem("说明")
        val exitItem = MenuItem("退出")

        // 添加事件监听器
        settingsItem.addActionListener {
            showSettingsWindow()
        }

        homeItem.addActionListener {
            showMainWindow()
        }

        aboutItem.addActionListener {
            showAboutDialog()
        }

        exitItem.addActionListener {
            exitApp()
        }

        // 添加菜单项到弹出菜单
        popupMenu.add(settingsItem)
        popupMenu.add(homeItem)
        popupMenu.add(aboutItem)
        popupMenu.addSeparator()
        popupMenu.add(exitItem)

        return popupMenu
    }

    /**
     * 显示主窗口
     */
    private fun showMainWindow() {
        SwingUtilities.invokeLater {
            // 这里需要调用显示主窗口的方法
            // 由于我们使用的是Compose Multiplatform，这可能需要特殊的处理
            DevLog.info("显示主窗口")
            // 可以考虑发送一个事件给主应用来显示窗口
        }
    }

    /**
     * 显示设置窗口
     */
    private fun showSettingsWindow() {
        SwingUtilities.invokeLater {
            DevLog.info("显示设置窗口")
            // 可以考虑发送一个事件给主应用来显示设置页面
        }
    }

    /**
     * 显示关于对话框
     */
    private fun showAboutDialog() {
        SwingUtilities.invokeLater {
            val aboutMessage = """
                devLogSuperMan
                
                版本: 1.0.0
                功能: SQL和ES DSL格式化工具
                作者: TreeRoot
                
                一个方便开发者的日志处理工具
            """.trimIndent()

            JOptionPane.showMessageDialog(
                null,
                aboutMessage,
                "关于 devLogSuperMan",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    /**
     * 退出应用
     */
    private fun exitApp() {
        SwingUtilities.invokeLater {
            // 停止剪贴板监控服务
            ClipboardMonitorService.stopMonitoring()

            // 退出应用
            System.exit(0)
        }
    }

    /**
     * 更新托盘图标以反映监控状态
     */
    fun updateTrayIconBasedOnStatus() {
        val isMonitoring = ClipboardMonitorService.isMonitoring()

        // 获取对应状态的图标
        val trayIconImage = if (isMonitoring) {
            createActiveTrayIcon() // 监控中使用绿色实心方块
        } else {
            createDefaultTrayIcon() // 停止时使用红色空心方块
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
     * 获取当前监控状态
     */
    fun getMonitoringStatus(): Boolean {
        return ClipboardMonitorService.isMonitoring()
    }

    /**
     * 创建默认托盘图标（监控状态：停止）
     */
    private fun createDefaultTrayIcon(): Image {
        return createTrayIcon("\u25A1", Color.RED) // 白色方块
    }

    /**
     * 创建监控中状态的托盘图标
     */
    private fun createActiveTrayIcon(): Image {
        return createTrayIcon("\u25A0", Color.GREEN) // 黑色方块
    }

    /**
     * 创建托盘图标
     * @param symbol 要绘制的符号
     * @param color 图标颜色
     */
    private fun createTrayIcon(symbol: String, color: Color): Image {
        val img = BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)
        val g = img.graphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // 绘制背景圆圈
        g.color = Color(240, 240, 240) // 浅灰色背景
        g.fillOval(2, 2, 28, 28)

        // 绘制边框
        g.color = Color.GRAY
        g.drawOval(2, 2, 28, 28)

        // 绘制状态符号
        g.color = color
        g.font = Font(g.font.name, Font.BOLD, 20)
        val fontMetrics = g.fontMetrics
        val width = fontMetrics.stringWidth(symbol)
        val height = fontMetrics.height
        g.drawString(symbol, (32 - width) / 2, (32 + height) / 2 - 2)

        g.dispose()
        return img
    }

    /**
     * 显示或隐藏托盘图标
     */
    fun setShowTrayIcon(show: Boolean) {
        if (show) {
            initializeTray()
        } else {
            removeTrayIcon()
        }
    }

    /**
     * 移除托盘图标
     */
    private fun removeTrayIcon() {
        systemTray?.let { tray ->
            trayIcon?.let { icon ->
                tray.remove(icon)
            }
        }
    }
}