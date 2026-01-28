package org.treeroot.devlog.ai

/**
 * AI服务使用示例
 * 展示如何使用AI适配器模式
 */
class AiServiceExample {

    /**
     * 初始化AI服务
     */
    fun initializeAiService(apiKey: String) {
        // 创建智谱AI适配器并设置为当前适配器
        val zhipuAdapter = AiAdapterManager.createZhipuAiAdapter(apiKey)
        AiAdapterManager.setCurrentAdapter(zhipuAdapter)
    }

    /**
     * 使用AI服务进行对话
     */
    suspend fun chatWithAi(message: String, model: String = "glm-4.7-flash"): String {
        val aiService = AiAdapterManager.getCurrentAdapter()
        return aiService.chat(message, model)
    }

    /**
     * 批量处理AI对话
     */
    suspend fun batchChatWithAi(messages: List<String>, model: String = "glm-4.7-flash"): List<String> {
        val aiService = AiAdapterManager.getCurrentAdapter()
        return aiService.batchChat(messages, model)
    }

    /**
     * 切换到不同的AI提供商
     */
    fun switchAiProvider(config: AiConfig) {
        val newAdapter = AiAdapterManager.createDefaultAdapter(config)
        AiAdapterManager.setCurrentAdapter(newAdapter)
    }
}
