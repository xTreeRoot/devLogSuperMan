package org.treeroot.devlog.ai

import ai.z.openapi.ZhipuAiClient
import ai.z.openapi.service.model.ChatCompletionCreateParams
import ai.z.openapi.service.model.ChatMessage
import ai.z.openapi.service.model.ChatMessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 智谱AI适配器
 * 实现AI服务接口，将智谱AI SDK适配到通用AI服务接口
 */
class ZhipuAiAdapter(private val apiKey: String) : AiService {

    private val client: ZhipuAiClient = ZhipuAiClient.builder()
        .ofZHIPU()
        .apiKey(apiKey)
        .build()

    override suspend fun chat(message: String, model: String): String = withContext(Dispatchers.IO) {
        try {
            val request = ChatCompletionCreateParams.builder()
                .model(model.takeIf { it != "default" } ?: "glm-4.7-flash")
                .messages(listOf(
                    ChatMessage.builder()
                        .role(ChatMessageRole.USER.value())
                        .content(message)
                        .build()
                ))
                .maxTokens(65536)
                .temperature(1.0f)
                .build()

            val response = client.chat().createChatCompletion(request)

            if (response.isSuccess) {
                response.data?.choices?.firstOrNull()?.message?.content ?: "未能获取AI回复"
            } else {
                "AI服务错误: ${response.msg}"
            }
        } catch (e: Exception) {
            "AI请求失败: ${e.message}"
        } as String
    }

    override suspend fun batchChat(messages: List<String>, model: String): List<String> = withContext(Dispatchers.IO) {
        messages.map { message ->
            chat(message, model)
        }
    }
}