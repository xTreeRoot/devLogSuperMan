package org.treeroot.devlog.ai

/**
 * AI适配器管理器
 * 提供工厂模式来创建和管理不同的AI适配器
 */
class AiAdapterManager {

    companion object {
        // 默认AI适配器
        private var currentAiAdapter: AiService? = null

        /**
         * 设置当前AI适配器
         */
        fun setCurrentAdapter(adapter: AiService) {
            currentAiAdapter = adapter
        }

        /**
         * 获取当前AI适配器
         */
        fun getCurrentAdapter(): AiService {
            return currentAiAdapter ?: throw IllegalStateException("AI适配器未初始化")
        }

        /**
         * 创建智谱AI适配器
         */
        fun createZhipuAiAdapter(apiKey: String): AiService {
            return ZhipuAiAdapter(apiKey)
        }

        /**
         * 创建默认适配器（可根据配置动态选择）
         */
        fun createDefaultAdapter(config: AiConfig): AiService {
            return when (config.provider) {
                AiProvider.ZHIPU -> createZhipuAiAdapter(config.apiKey)
                // 在此处可以添加更多AI提供商的适配器
                else -> createZhipuAiAdapter(config.apiKey) // 默认使用智谱AI
            }
        }
    }
}

/**
 * AI配置类
 */
data class AiConfig(
    val provider: AiProvider,
    val apiKey: String,
    val defaultModel: String = "default"
)

/**
 * AI提供商枚举
 */
enum class AiProvider {
    // 智谱AI
    ZHIPU,
    // OpenAI
    OPENAI,
    // 通义千问
    QWEN,
    // 其他AI服务
    OTHER
}