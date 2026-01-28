package org.treeroot.devlog.ai

/**
 * AI服务接口定义
 * 定义了AI服务的基本功能，便于后期替换不同的AI实现
 */
interface AiService {
    /**
     * 发送消息并获取AI回复
     * @param message 输入的消息
     * @param model AI模型名称
     * @return AI回复结果
     */
    suspend fun chat(message: String, model: String = "default"): String
    
    /**
     * 批量发送消息并获取AI回复
     * @param messages 消息列表
     * @param model AI模型名称
     * @return AI回复结果列表
     */
    suspend fun batchChat(messages: List<String>, model: String = "default"): List<String>
}