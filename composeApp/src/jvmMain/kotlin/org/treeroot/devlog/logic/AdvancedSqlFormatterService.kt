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
        
        val mybatisPatterns = listOf(
            "Preparing: ",
            "Parameters: ",
            "DEBUG ",
            "==>  Preparing: ",
            "==> Parameters: ",
            "<==      Total: ",
            "Creating a new SqlSession",
            "SqlSession ["
        )
        
        val lowerText = text.lowercase()
        return mybatisPatterns.any { pattern -> lowerText.contains(pattern.lowercase()) }
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
                val formattedSql = formatSql(sql, uppercaseKeywords = true, newlineAfterComma = true)
                results.add(formattedSql)
            }
            
            return if (results.isNotEmpty()) {
                results.joinToString("\n\n")
            } else {
                // 如果没有找到MyBatis格式，尝试普通SQL格式化
                formatSql(text)
            }
            
        } catch (e: Exception) {
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
        var paramIndex = 0
        
        for (param in params) {
            if (paramIndex >= countQuestionMarks(result)) {
                break
            }
            
            // 简单替换第一个遇到的问号
            result = replaceFirstQuestionMark(result, param)
            paramIndex++
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
        } catch (e: NumberFormatException) {
            false
        }
    }
    
    /**
     * 格式化SQL字符串，支持多种格式化选项
     * @param sql 待格式化的SQL字符串
     * @param indentSize 缩进大小
     * @param uppercaseKeywords 是否将关键字转为大写
     * @param newlineAfterComma 是否在逗号后换行
     * @return 格式化后的SQL字符串
     */
    fun formatSql(
        sql: String, 
        indentSize: Int = 4, 
        uppercaseKeywords: Boolean = true,
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
            formatted = if (uppercaseKeywords) {
                formatWithKeywords(formatted, indentSize)
            } else {
                formatWithoutKeywordCapitalization(formatted, indentSize)
            }
            
            // 处理逗号后的换行
            if (newlineAfterComma) {
                formatted = formatted.replace(", ", ",\n")
            }
            
            // 清理多余空白
            formatted = formatted.replace(Regex("\n\\s*\n"), "\n")
                .replace(Regex("\\s+"), " ")
                .trim()
            
            formatted
        } catch (e: Exception) {
            // 如果格式化失败，返回原始字符串
            sql
        }
    }
    
    /**
     * 使用MySQL风格的格式化
     */
    fun formatSqlMySqlStyle(sql: String): String {
        if (sql.isBlank()) {
            return sql
        }
        
        return try {
            var formatted = sql.trim()
            
            // 基本清理
            formatted = formatted.replace(Regex("\\s+"), " ")
            
            // MySQL风格格式化 - 使用更合适的换行和缩进
            formatted = formatWithMySqlStyle(formatted)
            
            // 清理多余空白
            formatted = formatted.replace(Regex("\n\\s*\n"), "\n")
                .replace(Regex("\\s+"), " ")
                .trim()
            
            formatted
        } catch (e: Exception) {
            // 如果格式化失败，返回原始字符串
            sql
        }
    }
    
    /**
     * 使用MySQL风格格式化SQL
     */
    private fun formatWithMySqlStyle(sql: String): String {
        var result = sql.uppercase()
        
        // 定义主要关键字，这些关键字前应该换行
        val majorKeywords = listOf(
            "SELECT", "FROM", "WHERE", "GROUP BY", "ORDER BY", "HAVING",
            "INSERT", "UPDATE", "DELETE", "CREATE", "ALTER", "DROP",
            "UNION", "UNION ALL", "JOIN", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "OUTER JOIN"
        )
        
        // 定义次要关键字，这些关键字前添加缩进但不一定换行
        val minorKeywords = listOf(
            "AS", "ON", "AND", "OR", "NOT", "IN", "EXISTS",
            "BETWEEN", "LIKE", "CASE", "WHEN", "THEN", "ELSE", "END",
            "LIMIT", "OFFSET", "VALUES", "SET", "INTO"
        )
        
        // 在主要关键字前添加换行和适当的缩进
        for (keyword in majorKeywords) {
            result = result.replace(
                Regex("\\b$keyword\\b"),
                "\n$keyword "
            )
        }
        
        // 处理FROM子句的特殊缩进
        result = result.replace(Regex("\\nFROM "), "\nFROM ")
        
        // 处理JOIN子句，使其与FROM对齐
        result = result.replace(Regex("\\n(JOIN|INNER JOIN|LEFT JOIN|RIGHT JOIN|OUTER JOIN) "), "\n    $1 ")
        
        // 处理ON子句，使其相对于JOIN缩进
        result = result.replace(Regex("\\nON "), "\n        ON ")
        
        // 处理WHERE子句，使其与FROM对齐
        result = result.replace(Regex("\\nWHERE "), "\nWHERE ")
        
        // 处理AND/OR子句，使其相对于WHERE缩进
        result = result.replace(Regex("\\n(AND|OR) "), "\n    $1 ")
        
        // 处理GROUP BY子句
        result = result.replace(Regex("\\nGROUP BY "), "\nGROUP BY ")
        
        // 处理HAVING子句
        result = result.replace(Regex("\\nHAVING "), "\nHAVING ")
        
        // 处理ORDER BY子句
        result = result.replace(Regex("\\nORDER BY "), "\nORDER BY ")
        
        // 处理LIMIT子句
        result = result.replace(Regex("\\nLIMIT "), "\nLIMIT ")
        
        // 清理多余的空白行
        result = result.replace(Regex("\\n\\s*\\n"), "\n")
        
        // 确保SELECT不在行首缩进
        if (result.startsWith("    SELECT ")) {
            result = result.substring(4)
        }
        
        // 处理逗号 - 在逗号后换行并缩进
        result = result.replace(", ", ",\n    ")
        
        // 清理多余的缩进
        result = result.replace(Regex("\\n\\s*\\n"), "\n")
        
        return result.trim()
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
            result = result.replace(Regex("\\n\\s*"), "\n")
                
            // 清理每一行前的空格
            result = result.lines().joinToString("\n") { it.trim() }
                
            result.trim()
        } catch (e: Exception) {
            // 如果格式化失败，返回原始字符串
            sql
        }
    }
        
    /**
     * 使用SQL关键字进行格式化（关键字大写）
     */
    private fun formatWithKeywords(sql: String, indentSize: Int): String {
        val keywords = listOf(
            "SELECT", "FROM", "WHERE", "GROUP BY", "ORDER BY", "HAVING",
            "JOIN", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "OUTER JOIN",
            "UNION", "UNION ALL", "INSERT", "UPDATE", "DELETE", "CREATE",
            "ALTER", "DROP", "AS", "ON", "AND", "OR", "NOT", "IN", "EXISTS",
            "BETWEEN", "LIKE", "CASE", "WHEN", "THEN", "ELSE", "END",
            "LIMIT", "OFFSET", "VALUES", "SET", "INTO", "TABLE", "DATABASE"
        )
            
        var result = sql.uppercase()
            
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
     * 不转换关键字大小写的格式化
     */
    private fun formatWithoutKeywordCapitalization(sql: String, indentSize: Int): String {
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
            Regex("\\n${" ".repeat(indentSize)}(SELECT|select|Select) "),
            "\n$1 "
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
        var inSingleQuote = false
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
                ch == '\'' && (i == 0 || sql[i - 1] != '\\') -> inSingleQuote = !inSingleQuote
                ch == '"' && (i == 0 || sql[i - 1] != '\\') -> inDoubleQuote = !inDoubleQuote
            }
            
            i++
        }
        
        return parenCount == 0 && bracketCount == 0 && braceCount == 0
    }
}