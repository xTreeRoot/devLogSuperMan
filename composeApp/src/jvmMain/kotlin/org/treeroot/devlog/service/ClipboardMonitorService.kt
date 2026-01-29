package org.treeroot.devlog.service

import kotlinx.coroutines.runBlocking
import org.treeroot.devlog.DevLog
import org.treeroot.devlog.business.EsDslFormatterService
import org.treeroot.devlog.business.SqlFormatterService
import org.treeroot.devlog.util.ClipboardHelper
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * 剪贴板监控服务
 * 用于自动检测和格式化剪贴板中的SQL或ES DSL内容
 */
object ClipboardMonitorService {
    private var scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var isMonitoring = false
    private var lastClipboardContent: String? = null
    private val sqlFormatterService = SqlFormatterService()
    private val esDslFormatterService = EsDslFormatterService()

    /**
     * 开始监控剪贴板
     */
    fun startMonitoring() {
        if (isMonitoring) return

        // 如果之前的调度器已关闭，则创建一个新的
        if (scheduler.isShutdown) {
            scheduler = Executors.newScheduledThreadPool(1)
        }

        isMonitoring = true
        lastClipboardContent = ClipboardHelper.getTextFromClipboard()

        // 立即执行一次监控，然后按固定频率继续监控
        monitorClipboard()

        scheduler.scheduleAtFixedRate({
            monitorClipboard()
        }, 100, 500, TimeUnit.MILLISECONDS) // 初始延迟100ms，然后每500ms检查一次
    }

    /**
     * 停止监控剪贴板
     */
    fun stopMonitoring() {
        isMonitoring = false
        if (!scheduler.isShutdown) {
            scheduler.shutdown()
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow()
                }
            } catch (_: InterruptedException) {
                scheduler.shutdownNow()
                Thread.currentThread().interrupt()
            }
        }

        // 重新创建调度器以备下次使用
        scheduler = Executors.newScheduledThreadPool(1)
    }


    /**
     * 监控剪贴板内容变化
     */
    private fun monitorClipboard() = runBlocking {
        val currentContent = ClipboardHelper.getTextFromClipboard()

        if (currentContent != null && currentContent != lastClipboardContent) {
            var isFormatted: Boolean
            // 通过服务来处理不同类型的格式化
            val formattedContent = processContentByType(currentContent)
            isFormatted = !formattedContent.isNullOrEmpty()

            // 如果进行了格式化操作且格式化结果不为空，则复制到剪贴板
            if (isFormatted && !formattedContent.isNullOrEmpty()) {
                DevLog.info("Formatted content: $formattedContent")
                ClipboardHelper.copyToClipboard(formattedContent)
                // 更新lastClipboardContent为格式化后的内容，避免重复处理
                lastClipboardContent = formattedContent
            } else {
                lastClipboardContent = currentContent
            }
        }
    }

    /**
     * 根据内容类型处理格式化
     * @param content 待处理的内容
     * @return 格式化后的内容，如果不需要格式化则返回null
     */
    private suspend fun processContentByType(content: String): String? {
        // 检查是否为MyBatis SQL格式
        if (sqlFormatterService.detectMybatisFormat(content)) {
            val result = sqlFormatterService.formatSqlWithPrettyStyle(content)
            return result
        }
        // 检查是否为ES DSL格式
        else if (esDslFormatterService.isEsQuery(content)) {
            val result = esDslFormatterService.extractAndFormatEsDsl(content)
            return result
        }

        // 如果都不匹配，返回null表示无需格式化
        return null
    }


    /**
     * 检查监控状态
     */
    fun isMonitoring(): Boolean {
        return isMonitoring
    }

}