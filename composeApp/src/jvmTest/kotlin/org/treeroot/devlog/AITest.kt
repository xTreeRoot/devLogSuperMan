package org.treeroot.devlog

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.treeroot.devlog.util.AiHelper


class AITest {

    @Test
    fun example() = runBlocking {
        // 使用配置文件中的API密钥初始化AI服务
        AiHelper.initialize()

        // 发送AI请求
        val response = AiHelper.sendRequest("1+1等于3")
        println(response)

        println("AI Test Example")
    }
}