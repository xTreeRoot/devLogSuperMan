package org.treeroot.devlog.logic

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.treeroot.devlog.util.ClipboardHelper

/**
 * ES DSL处理器的ViewModel
 * 管理ES DSL相关的UI状态和业务逻辑
 */
class EsDslViewModel {
    
    private val esDslService = EsDslFormatterService()
    
    // UI状态
    private val _originalDsl = mutableStateOf("")
    val originalDsl: State<String> = _originalDsl
    
    private val _formattedDsl = mutableStateOf("")
    val formattedDsl: State<String> = _formattedDsl
    
    private val _formattedResponse = mutableStateOf("")
    val formattedResponse: State<String> = _formattedResponse
    
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    private val _showDslTree = mutableStateOf(false)
    val showDslTree: State<Boolean> = _showDslTree
    
    private val _showResultTree = mutableStateOf(false)
    val showResultTree: State<Boolean> = _showResultTree
    
    /**
     * 更新原始ES DSL
     */
    fun updateOriginalDsl(dsl: String) {
        _originalDsl.value = dsl
    }
    
    /**
     * 格式化ES DSL
     */
    fun formatDsl() {
        if (_isLoading.value) return // 防止重复点击
        
        CoroutineScope(Dispatchers.Default).launch {
            _isLoading.value = true
            
            try {
                val (dsl, response) = esDslService.separateDslAndResponse(_originalDsl.value)
                _formattedDsl.value = dsl
                _formattedResponse.value = response
            } catch (e: Exception) {
                _formattedDsl.value = "格式化过程中出现错误: ${e.message}"
                _formattedResponse.value = ""
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 更新格式化后的响应内容
     */
    fun updateFormattedResponse(newText: String) {
        _formattedResponse.value = newText
    }

    /**
     * 更新格式化后的 DSL 内容
     * 用户在 EditableJSONTextView 编辑 DSL 时调用
     */
    fun updateFormattedDsl(newText: String) {
        _formattedDsl.value = newText
        // 同步更新 originalDsl，以便后续点击“格式化”时使用最新内容
        _originalDsl.value = newText
    }

    
    /**
     * 清空所有内容
     */
    fun clearAll() {
        _originalDsl.value = ""
        _formattedDsl.value = ""
    }
    
    /**
     * 复制格式化后的ES DSL到剪贴板
     */
    fun copyFormattedDslToClipboard() {
        if (_formattedDsl.value.isNotEmpty()) {
            ClipboardHelper.copyToClipboard(_formattedDsl.value)
        }
    }
    
    /**
     * 从剪贴板粘贴ES DSL到原始输入框
     */
    fun pasteFromClipboard() {
        val clipboardText = ClipboardHelper.getTextFromClipboard()
        if (!clipboardText.isNullOrEmpty()) {
            _originalDsl.value = clipboardText
        }
    }
    
    fun toggleDslTree() {
        _showDslTree.value = !_showDslTree.value
    }
    
    fun toggleResultTree() {
        _showResultTree.value = !_showResultTree.value
    }
}