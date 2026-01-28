package org.treeroot.devlog.config


data class ZhipuConfig(
    val api_key: String = "",
    val default_model: String = "glm-4.7-flash",
    val max_tokens: Int = 65536,
    val temperature: Float = 1.0f
)

data class OpenAiConfig(
    val api_key: String = "",
    val default_model: String = "gpt-4"
)

data class QwenConfig(
    val api_key: String = "",
    val default_model: String = "qwen-max"
)