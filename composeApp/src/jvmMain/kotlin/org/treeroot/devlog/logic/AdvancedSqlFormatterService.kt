package org.treeroot.devlog.logic

/**
 * 高级SQL格式化服务类
 * 提供更精细的SQL格式化功能，包括MyBatis日志解析
 */
class AdvancedSqlFormatterService {

    /**
     * 检测文本是否包含MyBatis日志格式
     */
    fun detectMybatisFormat(text: String): Boolean {
        if (text.isBlank()) {
            return false
        }

        val lowerText = text.lowercase()

        // 检查是否同时包含Preparing和Parameters，这是MyBatis日志的关键特征
        val hasPreparing = lowerText.contains("preparing:")
        val hasParameters = lowerText.contains("parameters:")

        if (hasPreparing && hasParameters) {
            // 如果上面的主要条件不满足，再检查其他辅助模式
            val mybatisPatterns = listOf(
                "==>  Preparing: ",
                "==> Parameters: ",
                "<==      Total: ",
                "Creating a new SqlSession",
                "SqlSession ["
            )
            return mybatisPatterns.any { pattern -> lowerText.contains(pattern.lowercase()) }
        }

        return false


    }

    /**
     * 从MyBatis日志中提取并格式化SQL
     */
    fun extractAndFormatMybatisSql(text: String): String {
        if (text.isBlank()) {
            return text
        }

        try {
            // 提取Preparing部分的SQL
            val preparingRegex = Regex("Preparing: ([^\n\r]+)", RegexOption.IGNORE_CASE)
            val parametersRegex = Regex("Parameters: ([^\n\r]+)", RegexOption.IGNORE_CASE)

            val preparingMatches = preparingRegex.findAll(text)
            val parametersMatches = parametersRegex.findAll(text)

            val results = mutableListOf<String>()

            preparingMatches.forEachIndexed { index, match ->
                var sql = match.groupValues[1].trim()

                // 替换参数占位符
                if (index < parametersMatches.count()) {
                    val paramsMatch = parametersMatches.elementAt(index)
                    val paramsStr = paramsMatch.groupValues[1]
                    val params = parseParameters(paramsStr)

                    sql = replaceParameters(sql, params)
                }

                // 格式化SQL
                val formattedSql = formatSql(sql, newlineAfterComma = true)
                results.add(formattedSql)
            }

            return if (results.isNotEmpty()) {
                results.joinToString("\n\n")
            } else {
                // 如果没有找到MyBatis格式，尝试普通SQL格式化
                formatSql(text)
            }

        } catch (_: Exception) {
            // 如果处理失败，返回原始文本
            return text
        }
    }

    /**
     * 解析参数字符串
     */
    private fun parseParameters(paramsStr: String): List<String> {
        val params = mutableListOf<String>()

        // 简单的参数分割，处理常见的参数格式
        val paramParts = paramsStr.split(", ")

        for (part in paramParts) {
            val cleanPart = part.trim()
            // 提取参数值，忽略类型信息
            val valueRegex = Regex("([^(]+)\\(")
            val match = valueRegex.find(cleanPart)
            if (match != null) {
                params.add(match.groupValues[1].trim())
            } else {
                // 如果没有找到类型信息，直接添加整个部分
                params.add(cleanPart)
            }
        }

        return params
    }

    /**
     * 将参数替换到SQL中
     */
    private fun replaceParameters(sql: String, params: List<String>): String {
        var result = sql

        for ((paramIndex, param) in params.withIndex()) {
            if (paramIndex >= countQuestionMarks(result)) {
                break
            }

            // 简单替换第一个遇到的问号
            result = replaceFirstQuestionMark(result, param)
        }

        return result
    }

    /**
     * 计算字符串中问号的数量
     */
    private fun countQuestionMarks(sql: String): Int {
        return sql.count { it == '?' }
    }

    /**
     * 替换SQL中第一个问号为参数值
     */
    private fun replaceFirstQuestionMark(sql: String, param: String): String {
        val quoteParam = if (isNumeric(param)) {
            param
        } else {
            "'$param'"
        }

        return sql.replaceFirst("?", quoteParam)
    }

    /**
     * 检查字符串是否为数值
     */
    private fun isNumeric(str: String): Boolean {
        return try {
            str.toDouble()
            true
        } catch (_: NumberFormatException) {
            false
        }
    }

    /**
     * 格式化SQL字符串，支持多种格式化选项
     * @param sql 待格式化的SQL字符串
     * @param indentSize 缩进大小
     * @param newlineAfterComma 是否在逗号后换行
     * @return 格式化后的SQL字符串
     */
    fun formatSql(
        sql: String,
        indentSize: Int = 4,
        newlineAfterComma: Boolean = true
    ): String {
        if (sql.isBlank()) {
            return sql
        }

        return try {
            var formatted = sql.trim()

            // 基本清理
            formatted = formatted.replace(Regex("\\s+"), " ")

            // 根据选项格式化
            formatted = formatWithKeywords(formatted, indentSize)

            // 处理逗号后的换行
            if (newlineAfterComma) {
                formatted = formatted.replace(", ", ",\n")
            }

            // 清理多余空白
            formatted = formatted.replace(Regex("\n\\s*\n"), "\n")
                .replace(Regex("\\s+"), " ")
                .trim()

            formatted
        } catch (_: Exception) {
            // 如果格式化失败，返回原始字符串
            sql
        }
    }

    /**
     * 每行一个节点的格式化方式
     * 实现每个SQL元素独占一行的格式化
     */
    fun formatSqlOneNodePerLine(sql: String): String {
        if (sql.isBlank()) {
            return sql
        }

        return try {
            var result = sql.trim()

            // 基本清理
            result = result.replace(Regex("\\s+"), " ")

            // 定义所有SQL关键字
            val sqlKeywords = listOf(
                "SELECT", "FROM", "WHERE", "GROUP BY", "ORDER BY", "HAVING",
                "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", "DROP",
                "UNION", "UNION ALL", "JOIN", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "OUTER JOIN",
                "AS", "ON", "AND", "OR", "NOT", "IN", "EXISTS",
                "BETWEEN", "LIKE", "CASE", "WHEN", "THEN", "ELSE", "END",
                "LIMIT", "OFFSET", "VALUES", "SET", "INTO", "DISTINCT",
                "COUNT", "SUM", "AVG", "MIN", "MAX", "IS NULL", "IS NOT NULL"
            )

            // 为每个关键字前添加换行
            for (keyword in sqlKeywords) {
                result = result.replace(
                    Regex("\\b$keyword\\b", RegexOption.IGNORE_CASE),
                    "\n$keyword"
                )
            }

            // 处理逗号 - 每个逗号后换行
            result = result.replace(",", ",\n")

            // 清理多余的空白
            result = result.replace(Regex("\\s+"), " ")

            // 移除开头的换行符
            result = result.trimStart('\n')

            // 标准化换行符前的空格
            result = result.replace(Regex("\n\\s*"), "\n")

            // 清理每一行前的空格
            result = result.lines().joinToString("\n") { it.trim() }

            result.trim()
        } catch (_: Exception) {
            // 如果格式化失败，返回原始字符串
            sql
        }
    }

    /**
     * 使用SQL关键字进行格式化（关键字大写）
     */
    private fun formatWithKeywords(sql: String, indentSize: Int): String {
        val keywords = listOf(
            "SELECT", "select", "Select",
            "FROM", "from", "From",
            "WHERE", "where", "Where",
            "GROUP BY", "group by", "Group By",
            "ORDER BY", "order by", "Order By",
            "HAVING", "having", "Having",
            "JOIN", "join", "Join",
            "INNER JOIN", "inner join", "Inner Join",
            "LEFT JOIN", "left join", "Left Join",
            "RIGHT JOIN", "right join", "Right Join",
            "OUTER JOIN", "outer join", "Outer Join",
            "UNION", "union", "Union",
            "UNION ALL", "union all", "Union All",
            "INSERT", "insert", "Insert",
            "UPDATE", "update", "Update",
            "DELETE", "delete", "Delete",
            "CREATE", "create", "Create",
            "ALTER", "alter", "Alter",
            "DROP", "drop", "Drop",
            "AS", "as", "As",
            "ON", "on", "On",
            "AND", "and", "And",
            "OR", "or", "Or",
            "NOT", "not", "Not",
            "IN", "in", "In",
            "EXISTS", "exists", "Exists",
            "BETWEEN", "between", "Between",
            "LIKE", "like", "Like",
            "CASE", "case", "Case",
            "WHEN", "when", "When",
            "THEN", "then", "Then",
            "ELSE", "else", "Else",
            "END", "end", "End",
            "LIMIT", "limit", "Limit",
            "OFFSET", "offset", "Offset",
            "VALUES", "values", "Values",
            "SET", "set", "Set",
            "INTO", "into", "Into",
            "TABLE", "table", "Table",
            "DATABASE", "database", "Database"
        )

        var result = sql

        // 为关键字添加换行和缩进
        keywords.forEach { keyword ->
            result = result.replace(
                Regex("\\b$keyword\\b"),
                "\n${" ".repeat(indentSize)}$keyword "
            )
        }

        // 特殊处理SELECT关键字（不需要缩进）
        result = result.replace(
            Regex("\n${" ".repeat(indentSize)}SELECT "),
            "\nSELECT "
        )

        return result
    }

    /**
     * 验证SQL语法基本正确性
     */
    fun validateSql(sql: String): Boolean {
        if (sql.isBlank()) {
            return false
        }

        // 基本的验证：检查括号是否匹配
        var parenCount = 0
        var bracketCount = 0
        var braceCount = 0
        val inSingleQuote = false
        var inDoubleQuote = false
        var i = 0

        while (i < sql.length) {
            val ch = sql[i]

            if (!inSingleQuote && !inDoubleQuote) {
                when (ch) {
                    '(' -> parenCount++
                    ')' -> parenCount--
                    '[' -> bracketCount++
                    ']' -> bracketCount--
                    '{' -> braceCount++
                    '}' -> braceCount--
                }
            }

            when {
                ch == '"' && (i == 0 || sql[i - 1] != '\\') -> inDoubleQuote = !inDoubleQuote
            }

            i++
        }

        return parenCount == 0 && bracketCount == 0 && braceCount == 0
    }

    /**
     * 从日志中提取SQL语句并替换参数
     */
    private fun extractSqlFromLog(logText: String): String {
        // 检测是否为MyBatis日志格式
        val preparingRegex = Regex("Preparing: ([^\n\r]+)", RegexOption.IGNORE_CASE)
        val parametersRegex = Regex("Parameters: ([^\n\r]+)", RegexOption.IGNORE_CASE)

        val preparingMatches = preparingRegex.findAll(logText)
        val parametersMatches = parametersRegex.findAll(logText)

        val results = mutableListOf<String>()

        preparingMatches.forEachIndexed { index, match ->
            var sql = match.groupValues[1].trim()

            // 替换参数占位符
            if (index < parametersMatches.count()) {
                val paramsMatch = parametersMatches.elementAt(index)
                val paramsStr = paramsMatch.groupValues[1]
                val params = parseParameters(paramsStr)

                sql = replaceParameters(sql, params)
            }

            results.add(sql)
        }

        // 如果找到了SQL语句，则返回它们
        if (results.isNotEmpty()) {
            return results.joinToString(";")
        }

        // 如果没有找到MyBatis格式的SQL，则返回原始文本
        return logText
    }

    /**
     *  纯粹的 MySQL SQL 格式化器，支持多条 SQL
     * 输出格式等同于:
     *
     * SELECT id,
     *        xx,
     *        yy
     * FROM table
     * WHERE ...
     * ORDER BY ...
     */
    suspend fun formatMySqlPretty(raw: String): String =
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            // 先提取SQL，再格式化
            val extractedSql = extractSqlFromLog(raw)
            extractedSql.split(";")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .joinToString("\n\n") { sql ->
                    formatSingleSql(sql)
                }
        }

    /**
     * 格式化单条 SQL
     */
    private fun formatSingleSql(sql: String): String {
        var s = sql.trim()

        // SELECT 字段格式化 - 先处理SELECT后的字段
        s = s.replace(Regex("(?i)SELECT\\s+"), "SELECT\n       ")

        // 标准关键字前强制换行
        s = s.replace(Regex("(?i)\\bLEFT JOIN\\b"), "\nLEFT JOIN")
        s = s.replace(Regex("(?i)\\bRIGHT JOIN\\b"), "\nRIGHT JOIN")
        s = s.replace(Regex("(?i)\\bOUTER JOIN\\b"), "\nOUTER JOIN")
        s = s.replace(Regex("(?i)\\bINNER JOIN\\b"), "\nINNER JOIN")
        s = s.replace(Regex("(?i)\\bGROUP BY\\b"), "\nGROUP BY")
        s = s.replace(Regex("(?i)\\bORDER BY\\b"), "\nORDER BY")
        s = s.replace(Regex("(?i)\\bFROM\\b"), "\nFROM")
        s = s.replace(Regex("(?i)\\bWHERE\\b"), "\nWHERE")
        //s = s.replace(Regex("(?i)\\bJOIN\\b"), "\nJOIN")

        // 逗号后换行并对齐
        s = s.replace(",", ",\n       ")

        // 多个空行压缩成 1 行
        s = s.replace(Regex("\n{3,}"), "\n\n")

        return s.trim()
    }

    /**
     * 使用MySQL风格格式化SQL - 使用新的pretty格式化方法
     */
    suspend fun formatSqlWithPrettyStyle(sql: String): String {
        if (sql.isBlank()) {
            return sql
        }

        return try {
            // 调用内部的formatMySqlPretty函数
            formatMySqlPretty(sql)
        } catch (_: Exception) {
            // 如果格式化失败，返回原始字符串
            sql
        }
    }
}