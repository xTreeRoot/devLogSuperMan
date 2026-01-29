package org.treeroot.devlog.service

import org.treeroot.devlog.DevLog
import org.treeroot.devlog.logic.AdvancedSqlFormatterService
import org.treeroot.devlog.logic.EsDslFormatterService
import org.treeroot.devlog.util.ClipboardHelper
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.SwingUtilities

/**
 * 剪贴板监控服务
 * 用于自动检测和格式化剪贴板中的SQL或ES DSL内容
 */
class ClipboardMonitorService {
    private var scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var isMonitoring = false
    private var lastClipboardContent: String? = null
    private val sqlFormatterService = AdvancedSqlFormatterService()
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
     * 检测文本是否包含ES DSL格式
     * @param text 待检测文本
     * @return true 表示可能是ES查询或响应
     */
    fun detectEsFormat(text: String): Boolean {
        if (text.isBlank()) return false

        // 检查是否为有效的JSON结构
        if (!isValidJsonStructure(text)) return false

        val esIndicators = listOf(
            "\"query\"",
            "\"bool\"",
            "\"match\"",
            "\"term\"",
            "\"must\"",
            "\"should\"",
            "\"filter\"",
            "\"aggs\"",
            "\"aggregations\"",
            "\"size\"",
            "\"from\"",
            "\"sort\"",
            "_source",
            "took",
            "hits",
            "timed_out",
            "_shards"
        )

        val lowerText = text.lowercase()
        return esIndicators.any { indicator -> lowerText.contains(indicator) }
    }

    /**
     * 检查文本是否为有效的JSON结构
     * @param text 待检测文本
     * @return true 表示可能是有效的JSON
     */
    private fun isValidJsonStructure(text: String): Boolean {
        val trimmed = text.trim()
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
               (trimmed.startsWith("[") && trimmed.endsWith("]"))
    }
    /**
     * 监控剪贴板内容变化
     */
    private fun monitorClipboard() {
        val currentContent = ClipboardHelper.getTextFromClipboard()

        if (currentContent != null && currentContent != lastClipboardContent) {
            DevLog.info("Clipboard changed, content: $currentContent")

            var formattedContent: String? = null
            var isFormatted = false

            // 检查是否为MyBatis SQL格式
            if (sqlFormatterService.detectMybatisFormat(currentContent)) {
                DevLog.info("Detected MyBatis SQL format")
                formattedContent = sqlFormatterService.extractAndFormatMybatisSql(currentContent)
                isFormatted = true
                DevLog.info("MyBatis formatted result: $formattedContent")
            }
            // 检查是否为ES DSL格式
            else if (esDslFormatterService.isEsQuery(currentContent)) {
                DevLog.info("Detected ES DSL format")
                formattedContent = esDslFormatterService.extractAndFormatEsDsl(currentContent)
                isFormatted = true
                DevLog.info("ES DSL formatted result: $formattedContent")
            }
            // 最后检查普通SQL格式 (最通用的)
            else if (isSqlFormat(currentContent)) {
                DevLog.info("Detected SQL format")
                formattedContent = sqlFormatterService.formatSql(currentContent)
                isFormatted = true
                DevLog.info("SQL formatted result: $formattedContent")
            }

            // 如果进行了格式化操作且格式化结果不为空，则复制到剪贴板
            if (isFormatted && !formattedContent.isNullOrEmpty()) {
                DevLog.info("Formatted content: $formattedContent")
                val copyResult = ClipboardHelper.copyToClipboard(formattedContent)
                DevLog.info("Copy to clipboard result: $copyResult")
                DevLog.info("Copied formatted content to clipboard")
                // 更新lastClipboardContent为格式化后的内容，避免重复处理
                lastClipboardContent = formattedContent
            } else {
                DevLog.info("No formatting needed or formatting failed, isFormatted: $isFormatted, formattedContent is null or empty: ${formattedContent.isNullOrEmpty()}")
                lastClipboardContent = currentContent
            }
        }
    }

    /**
     * 检测文本是否包含SQL格式
     * @param text 待检测文本
     * @return true 表示可能是SQL语句
     */
    private fun isSqlFormat(text: String): Boolean {
        if (text.isBlank()) return false

        val sqlIndicators = listOf(
            "SELECT", "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", "DROP",
            "select", "insert", "update", "delete", "create", "alter", "drop"
        )

        val trimmedText = text.trim().uppercase()
        return sqlIndicators.any { indicator -> trimmedText.contains(indicator) }
    }



    /**
     * 检查监控状态
     */
    fun isMonitoring(): Boolean {
        return isMonitoring
    }

}