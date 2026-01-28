package org.treeroot.devlog.util

import org.treeroot.devlog.ai.AiServiceExample
import org.treeroot.devlog.ai.AiConfig
import org.treeroot.devlog.ai.AiProvider
import org.treeroot.devlog.config.ConfigManager
import kotlinx.coroutines.*

/**
 * AI助手工具类
 * 提供便捷的AI服务访问接口
 */
object AiHelper {
    
    private val aiExample = AiServiceExample()
    private var isInitialized = false
    
    /**
     * 初始化AI服务
     */
    fun initialize(apiKey: String? = null) {
        if (!isInitialized) {
            val key = apiKey ?: ConfigManager.instance.getZhipuConfig().api_key
            aiExample.initializeAiService(key)
            isInitialized = true
        }
    }
    
    /**
     * 异步发送AI请求
     */
    suspend fun sendRequest(message: String, model: String = "glm-4.7-flash"): String {
        if (!isInitialized) {
            throw IllegalStateException("AI服务未初始化，请先调用initialize方法")
        }
        return aiExample.chatWithAi(message, model)
    }
    
    /**
     * 同步发送AI请求（包装为挂起函数）
     */
    fun sendRequestSync(message: String, model: String = "glm-4.7-flash"): Deferred<String> {
        return CoroutineScope(Dispatchers.IO).async {
            sendRequest(message, model)
        }
    }
    
    /**
     * 批量发送AI请求
     */
    suspend fun sendBatchRequest(messages: List<String>, model: String = "glm-4.7-flash"): List<String> {
        if (!isInitialized) {
            throw IllegalStateException("AI服务未初始化，请先调用initialize方法")
        }
        return aiExample.batchChatWithAi(messages, model)
    }
    
    /**
     * 切换AI提供商
     */
    fun switchProvider(provider: AiProvider, apiKey: String, defaultModel: String = "default") {
        val config = AiConfig(provider, apiKey, defaultModel)
        aiExample.switchAiProvider(config)
    }
    
    /**
     * 检查AI服务是否已初始化
     */
    fun isInitialized(): Boolean {
        return isInitialized
    }
}