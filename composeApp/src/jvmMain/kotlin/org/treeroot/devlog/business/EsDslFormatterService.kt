package org.treeroot.devlog.business

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

/**
 * ES DSL格式化服务
 */
class EsDslFormatterService {

    private val gsonPretty = GsonBuilder().setPrettyPrinting().create()


    /** 格式化JSON */
    fun formatJson(jsonString: String): String {
        if (jsonString.isBlank()) return jsonString
        return try {
            val jsonElement = JsonParser.parseString(jsonString)
            gsonPretty.toJson(jsonElement)
        } catch (_: Exception) {
            jsonString
        }
    }

    /** 提取并格式化ES DSL */
    fun extractAndFormatEsDsl(text: String): String {
        val (dsl, _) = separateDslAndResponse(text)
        return formatJson(dsl)
    }

    /** 从文本中提取JSON（首个JSON对象） */
    fun extractJsonFromText(text: String): String {
        val trimmed = text.trim()
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) ||
            (trimmed.startsWith("[") && trimmed.endsWith("]"))
        ) return trimmed
        return extractAllJsonBlocks(text).firstOrNull() ?: ""
    }

    /** 提取文本中所有完整JSON块（支持嵌套） */
    fun extractAllJsonBlocks(text: String): List<String> {
        val result = mutableListOf<String>()
        var braceCount = 0
        var startIndex = -1
        text.forEachIndexed { index, c ->
            when (c) {
                '{' -> {
                    if (braceCount == 0) startIndex = index
                    braceCount++
                }

                '}' -> {
                    braceCount--
                    if (braceCount == 0 && startIndex >= 0) {
                        val block = text.substring(startIndex, index + 1)
                        if (isValidJson(block)) result.add(block)
                        startIndex = -1
                    }
                }
            }
        }
        return result
    }

    /** 检查字符串是否有效JSON */
    private fun isValidJson(jsonString: String): Boolean {
        if (jsonString.isBlank()) return false
        return try {
            JsonParser.parseString(jsonString); true
        } catch (_: Exception) {
            false
        }
    }


    /** 分离DSL和响应 */
    fun separateDslAndResponse(text: String): Pair<String, String> {
        if (text.isBlank()) return Pair("", "")
        return try {
            // 首先检查是否包含curl命令和HTTP响应，这种情况需要处理原始文本
            when {
                // 处理包含HTTP响应的情况（以#开头的行通常是HTTP响应）
                text.contains("curl") && text.contains("# HTTP/") -> {
                    extractCurlRequestAndResponseFromFullLog(text)
                }
                // 处理包含curl命令的情况
                else -> {
                    // 清理文本，移除日志前缀行，保留可能包含curl命令或JSON的部分
                    val cleanedText = cleanLogText(text)

                    when {
                        cleanedText.contains("curl") && cleanedText.contains("-X") -> {
                            extractCurlRequestAndResponse(cleanedText)
                        }

                        else -> {
                            val allJsonBlocks = extractAllJsonBlocks(cleanedText)
                            if (allJsonBlocks.size >= 2) {
                                // 如果有多个JSON块，尝试识别哪个是查询，哪个是响应
                                val queryBlock = allJsonBlocks.firstOrNull { block ->
                                    isEsQuery(block) || hasQueryIndicators(block)
                                }
                                val responseBlock = allJsonBlocks.firstOrNull { block ->
                                    isResponse(block) || hasResponseIndicators(block)
                                }

                                // 如果找到了查询和响应，返回它们；否则返回第一个和第二个
                                if (queryBlock != null && responseBlock != null) {
                                    Pair(formatJson(queryBlock), formatJson(responseBlock))
                                } else {
                                    // 尝试按顺序分配：第一个作为查询，第二个作为响应
                                    val query = if (isEsQuery(allJsonBlocks[0])) allJsonBlocks[0] else queryBlock
                                        ?: allJsonBlocks[0]
                                    val response = if (allJsonBlocks.size > 1) {
                                        if (isResponse(allJsonBlocks[1])) allJsonBlocks[1] else responseBlock
                                            ?: allJsonBlocks[1]
                                    } else ""
                                    Pair(formatJson(query), formatJson(response))
                                }
                            } else {
                                // 如果只有一个或没有JSON块，按照原来的逻辑
                                val queryJson = extractQueryJson(cleanedText)
                                val responseJson = extractResponseJson(cleanedText)
                                Pair(formatJson(queryJson), formatJson(responseJson))
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {
            val formatted = formatJson(text)
            if (isEsQuery(formatted)) Pair(formatted, "") else Pair("", formatted)
        }
    }

    /**
     * 清理日志文本，移除日志级别标识行，保留包含DSL或响应的部分
     */
    private fun cleanLogText(text: String): String {
        val lines = text.lines()
        val resultLines = mutableListOf<String>()

        for (line in lines) {
            // 移除包含日志时间戳和日志级别的行（如 DEBUG, WARN, TRACE, INFO, ERROR）
            if (!line.matches(logPattern)) {
                resultLines.add(line)
            }
        }

        return resultLines.joinToString("\n").trim()
    }

    companion object {
        // 正则表达式匹配日志行（包含日期时间戳和日志级别）
        private val logPattern =
            Regex("^\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2}:\\d{2}\\s+(DEBUG|INFO|WARN|ERROR|TRACE)\\s+.*")
    }

    /** 从curl命令提取请求和响应 */
    private fun extractCurlRequestAndResponse(text: String): Pair<String, String> {
        val dataPattern = Regex(
            """(-d|--data|--data-ascii|--data-binary)\s+(['"])(.*?)\2""",
            setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL)
        )
        val match = dataPattern.find(text)
        var dsl = match?.groups?.get(3)?.value ?: ""
        if (match != null && match.groups[2]?.value == "'") {
            // 单引号模式下处理转义
            dsl = dsl.replace("\\'", "'").replace("\\\"", "\"").replace("\\\\", "\\")
        }
        val response = extractResponseFromCurlOutput(text)
        return Pair(dsl, response)
    }

    /** 从curl输出提取响应 */
    private fun extractResponseFromCurlOutput(text: String): String {
        val lines = text.lines()
        val sb = StringBuilder()
        var inResponse = false
        for (line in lines) {
            if (line.startsWith("# HTTP/")) {
                inResponse = true; continue
            }
            if (!inResponse) continue
            if (line.startsWith("#")) continue
            sb.append(line)
            sb.append("\n")
        }
        val responseText = sb.toString()
        return if (isValidJson(responseText)) responseText else extractJsonFromText(responseText)
    }

    /** 从完整日志中提取curl请求和响应 */
    private fun extractCurlRequestAndResponseFromFullLog(text: String): Pair<String, String> {
        // 先提取curl命令中的DSL
        val dsl = extractDslFromCurlCommand(text)

        // 然后提取HTTP响应部分
        val response = extractResponseFromFullLog(text)

        return Pair(dsl, response)
    }

    /** 从完整日志中提取curl命令DSL */
    private fun extractDslFromCurlCommand(text: String): String {
        val dataPattern = Regex(
            """(-d|--data|--data-ascii|--data-binary)\s+(['"])(.*?)\2""",
            setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL)
        )
        val match = dataPattern.find(text)
        var dsl = match?.groups?.get(3)?.value ?: ""
        if (match != null && match.groups[2]?.value == "'") {
            // 单引号模式下处理转义
            dsl = dsl.replace("\\'", "'").replace("\\\"", "\"").replace("\\\\", "\\")
        }
        return dsl
    }

    /** 从完整日志中提取响应 */
    private fun extractResponseFromFullLog(text: String): String {
        val lines = text.lines()
        val sb = StringBuilder()
        var inResponse = false
        for (line in lines) {
            if (line.startsWith("# HTTP/")) {
                inResponse = true; continue
            }
            if (!inResponse) continue
            // 对于以#开头的行，如果是JSON响应部分，我们需要去掉#前缀
            if (line.startsWith("#")) {
                val content = line.substring(1).trimStart() // 去掉#和后面的空格
                if (content.isNotBlank()) {
                    sb.append(content)
                }
            } else {
                sb.append(line)
            }
        }
        val responseText = sb.toString().trim()
        return if (isValidJson(responseText)) responseText else extractJsonFromText(responseText)
    }

    /** 是否是ES查询 */
    fun isEsQuery(jsonString: String): Boolean {
        val indicators = listOf("query", "match", "term", "bool", "must", "should", "filter")
        return indicators.any { jsonString.lowercase().contains("\"$it\"") }
    }

    /** 是否是ES响应 */
    fun isResponse(jsonString: String): Boolean {
        val indicators = listOf("took", "hits", "timed_out", "_shards", "aggregations")
        return indicators.any { jsonString.lowercase().contains("\"$it\"") }
    }

    /** 检查是否包含查询指示符 */
    private fun hasQueryIndicators(jsonString: String): Boolean {
        val indicators = listOf("query", "aggs", "aggregations", "size", "from", "sort")
        return indicators.any { jsonString.lowercase().contains("\"$it\"") }
    }

    /** 检查是否包含响应指示符 */
    private fun hasResponseIndicators(jsonString: String): Boolean {
        val indicators = listOf("took", "hits", "timed_out", "_shards", "aggregations")
        return indicators.any { jsonString.lowercase().contains("\"$it\"") }
    }

    /** 提取可能的查询JSON */
    private fun extractQueryJson(text: String): String {
        val indicators = listOf("query", "aggs", "aggregations", "size", "from", "sort")
        return extractAllJsonBlocks(text).firstOrNull { block ->
            indicators.any { block.contains("\"$it\"") }
        } ?: ""
    }

    /** 提取可能的响应JSON */
    private fun extractResponseJson(text: String): String {
        val indicators = listOf("took", "hits", "timed_out", "_shards", "aggregations")
        return extractAllJsonBlocks(text).firstOrNull { block ->
            indicators.any { block.contains("\"$it\"") }
        } ?: ""
    }
}