package org.treeroot.devlog.logic

/**
 * SQL格式化服务类
 * 提供SQL语句的格式化功能
 */
class SqlFormatterService {
    
    /**
     * 格式化SQL字符串
     * @param sql 待格式化的SQL字符串
     * @return 格式化后的SQL字符串
     */
    fun formatSql(sql: String): String {
        if (sql.isBlank()) {
            return sql
        }
        
        return try {
            // 基本的SQL格式化处理
            sql.trim()
                .replace(Regex("\\s+"), " ") // 将多个空白字符替换为单个空格
                .replace(Regex("\\s*([(),])\\s*")) { match ->
                    // 处理括号前后的空格
                    val separator = if (match.value.trim() == ",") ", " else match.value.trim()
                    "${match.groups[1]?.value}$separator"
                }
                .replace(Regex("\\s*(=|!=|<>|<|>|<=|>=|\\+|-|\\*|/)\\s*")) { match ->
                    // 处理操作符周围的空格
                    " ${match.value.trim()} "
                }
                .let { formatted ->
                    // 按关键字分割并格式化缩进
                    formatWithKeywords(formatted)
                }
                .trim()
        } catch (e: Exception) {
            // 如果格式化失败，返回原始字符串
            sql
        }
    }
    
    /**
     * 使用SQL关键字进行格式化
     */
    private fun formatWithKeywords(sql: String): String {
        val keywords = listOf(
            "SELECT", "FROM", "WHERE", "GROUP BY", "ORDER BY", "HAVING",
            "JOIN", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "OUTER JOIN",
            "UNION", "UNION ALL", "INSERT", "UPDATE", "DELETE", "CREATE",
            "ALTER", "DROP", "AS", "ON", "AND", "OR", "NOT", "IN", "EXISTS",
            "BETWEEN", "LIKE", "CASE", "WHEN", "THEN", "ELSE", "END",
            "LIMIT", "OFFSET", "VALUES", "SET", "INTO", "TABLE", "DATABASE"
        )
        
        var result = sql
        
        // 为关键字添加换行和缩进
        keywords.forEach { keyword ->
            result = result.replace(
                Regex("\\b$keyword\\b", RegexOption.IGNORE_CASE),
                "\n$keyword "
            )
        }
        
        // 清理多余的换行和空格
        result = result.replace(Regex("\n\\s*\n"), "\n")
            .replace(Regex("\n\\s*,"), ", ")
            .replace(Regex("\\s+"), " ")
        
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
        var inSingleQuote = false
        var inDoubleQuote = false
        var i = 0
        
        while (i < sql.length) {
            val ch = sql[i]
            
            if (!inSingleQuote && !inDoubleQuote) {
                when (ch) {
                    '(' -> parenCount++
                    ')' -> parenCount--
                }
            }
            
            when {
                ch == '\'' && (i == 0 || sql[i - 1] != '\\') -> inSingleQuote = !inSingleQuote
                ch == '"' && (i == 0 || sql[i - 1] != '\\') -> inDoubleQuote = !inDoubleQuote
            }
            
            i++
        }
        
        return parenCount == 0
    }
}