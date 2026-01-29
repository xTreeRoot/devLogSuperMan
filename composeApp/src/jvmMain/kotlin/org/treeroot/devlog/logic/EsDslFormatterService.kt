package org.treeroot.devlog.logic

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
       return dsl
    }

    /** 从文本中提取JSON（首个JSON对象） */
    fun extractJsonFromText(text: String): String {
        val trimmed = text.trim()
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) ||
            (trimmed.startsWith("[") && trimmed.endsWith("]"))) return trimmed
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
        return try { JsonParser.parseString(jsonString); true } catch (_: Exception) { false }
    }


    /** 分离DSL和响应 */
    fun separateDslAndResponse(text: String): Pair<String, String> {
        if (text.isBlank()) return Pair("", "")
        return try {
            when {
                text.contains("curl") && text.contains("-X") -> extractCurlRequestAndResponse(text)
                else -> {
                    val queryJson = extractQueryJson(text)
                    val responseJson = extractResponseJson(text)
                    Pair(formatJson(queryJson), formatJson(responseJson))
                }
            }
        } catch (_: Exception) {
            val formatted = formatJson(text)
            if (isEsQuery(formatted)) Pair(formatted, "") else Pair("", formatted)
        }
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
            if (line.startsWith("# HTTP/")) { inResponse = true; continue }
            if (!inResponse) continue
            if (line.startsWith("#")) continue
            sb.append(line)
            sb.append("\n")
        }
        val responseText = sb.toString()
        return if (isValidJson(responseText)) responseText else extractJsonFromText(responseText)
    }


    /** 是否是ES查询 */
    fun isEsQuery(jsonString: String): Boolean {
        val indicators = listOf("query","match","term","bool","must","should","filter")
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
        val indicators = listOf("took","hits","timed_out","_shards","aggregations")
        return extractAllJsonBlocks(text).firstOrNull { block ->
            indicators.any { block.contains("\"$it\"") }
        } ?: ""
    }
}