package org.treeroot.devlog.service

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
    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var isMonitoring = false
    private var lastClipboardContent: String? = null
    
    private val sqlFormatterService = AdvancedSqlFormatterService()
    private val esDslFormatterService = EsDslFormatterService()
    
    /**
     * 开始监控剪贴板
     */
    fun startMonitoring() {
        if (isMonitoring) return
        
        isMonitoring = true
        lastClipboardContent = ClipboardHelper.getTextFromClipboard()
        
        scheduler.scheduleAtFixedRate({
            monitorClipboard()
        }, 0, 1, TimeUnit.SECONDS)
    }
    
    /**
     * 停止监控剪贴板
     */
    fun stopMonitoring() {
        isMonitoring = false
        scheduler.shutdown()
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow()
            }
        } catch (e: InterruptedException) {
            scheduler.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
    /**
     * 检测文本是否包含ES DSL格式
     * @param text 待检测文本
     * @return true 表示可能是ES查询或响应
     */
    fun detectEsFormat(text: String): Boolean {
        if (text.isBlank()) return false

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
     * 监控剪贴板内容变化
     */
    private fun monitorClipboard() {
        val currentContent = ClipboardHelper.getTextFromClipboard()
        
        if (currentContent != null && currentContent != lastClipboardContent) {
            // 检查是否为MyBatis SQL格式
            if (sqlFormatterService.detectMybatisFormat(currentContent)) {
                SwingUtilities.invokeLater {
                    val formattedSql = sqlFormatterService.extractAndFormatMybatisSql(currentContent)
                    ClipboardHelper.copyToClipboard(formattedSql)
                }
                lastClipboardContent = currentContent
                return
            }
            
            // 检查是否为ES DSL格式
            if (detectEsFormat(currentContent)) {
                SwingUtilities.invokeLater {
                    val formattedDsl = esDslFormatterService.extractAndFormatEsDsl(currentContent)
                    ClipboardHelper.copyToClipboard(formattedDsl)
                }
                lastClipboardContent = currentContent
                return
            }
            
            lastClipboardContent = currentContent
        }
    }


    
    /**
     * 检查监控状态
     */
    fun isMonitoring(): Boolean {
        return isMonitoring
    }

}