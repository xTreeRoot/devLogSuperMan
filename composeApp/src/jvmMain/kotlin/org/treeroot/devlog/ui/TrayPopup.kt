package org.treeroot.devlog.ui

import org.treeroot.devlog.DevLog
import org.treeroot.devlog.constant.ResourceConstant
import org.treeroot.devlog.service.ClipboardMonitorService
import org.treeroot.devlog.service.SystemTrayService.initializeTray
import org.treeroot.devlog.util.ImageUtils
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Image
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.RenderingHints
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

object TrayPopup {
    fun createPopupMenu(): PopupMenu {
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
     * 显示设置窗口
     */
    fun showSettingsWindow() {
        SwingUtilities.invokeLater {
            DevLog.info("显示设置窗口")
        }
    }

    /**
     * 显示主窗口
     */
    fun showMainWindow() {
        SwingUtilities.invokeLater {
            DevLog.info("显示主窗口")
        }
    }

    /**
     * 显示关于对话框
     */
    fun showAboutDialog() {
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
                JOptionPane.INFORMATION_MESSAGE,
                ImageUtils.imageToIcon(ResourceConstant.ATM_PATH, 100, 100)
            )
        }
    }

    /**
     * 退出应用
     */
    fun exitApp() {
        SwingUtilities.invokeLater {
            // 停止剪贴板监控服务
            ClipboardMonitorService.stopMonitoring()

            // 退出应用
            exitProcess(0)
        }
    }

    /**
     * 显示或隐藏托盘图标
     */
    fun setShowTrayIcon(show: Boolean,systemTray: SystemTray?, trayIcon: TrayIcon?) {
        if (show) {
            initializeTray()
        } else {
            removeTrayIcon(systemTray, trayIcon)
        }
    }

    /**
     * 移除托盘图标
     */
    private fun removeTrayIcon(systemTray: SystemTray?, trayIcon: TrayIcon?) {
        systemTray?.let { tray ->
            trayIcon?.let { icon ->
                tray.remove(icon)
            }
        }
    }



    /**
     * 创建托盘图标
     * @param symbol 要绘制的符号
     * @param color 图标颜色
     */
    fun createTrayIcon(symbol: String, color: Color): Image {
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
}