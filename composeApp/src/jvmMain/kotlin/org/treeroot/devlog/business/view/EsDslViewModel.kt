package org.treeroot.devlog.business.view

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.treeroot.devlog.DevLog
import org.treeroot.devlog.business.EsDslFormatterService
import org.treeroot.devlog.logic.model.EsDslResult
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

    private val _result = mutableStateOf(EsDslResult(success = true))
    val result: State<EsDslResult> = _result

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _showDslTree = mutableStateOf(false)
    val showDslTree: State<Boolean> = _showDslTree

    private val _showResultTree = mutableStateOf(false)
    val showResultTree: State<Boolean> = _showResultTree

    // 为了向后兼容，提供原有的属性访问
    val formattedDsl: State<String> = object : State<String> {
        override val value: String
            get() = _result.value.formattedDsl
    }

    val formattedResponse: State<String> = object : State<String> {
        override val value: String
            get() = _result.value.formattedResponse
    }

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
                val dslType = if (esDslService.isEsQuery(dsl)) "query" else "response"

                _result.value = EsDslResult(
                    success = true,
                    formattedDsl = dsl,
                    formattedResponse = response,
                    processingTime = System.currentTimeMillis(),
                    errorMessage = null,
                    dslType = dslType
                )
            } catch (e: Exception) {
                _result.value = EsDslResult(
                    success = false,
                    formattedDsl = "",
                    formattedResponse = "",
                    processingTime = System.currentTimeMillis(),
                    errorMessage = "格式化过程中出现错误: ${'$'}{e.message}"
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 更新格式化后的响应内容
     */
    fun updateFormattedResponse(newText: String) {
        _result.value = _result.value.copy(formattedResponse = newText)
    }

    /**
     * 更新格式化后的 DSL 内容
     */
    fun updateFormattedDsl(newText: String) {
        _result.value = _result.value.copy(formattedDsl = newText)
        // 同步更新 originalDsl，以便后续点击"格式化"时使用最新内容
        _originalDsl.value = newText
    }


    /**
     * 复制格式化后的ES DSL到剪贴板
     */
    fun copyFormattedDslToClipboard() {
        if (result.value.formattedDsl.isNotEmpty()) {
            ClipboardHelper.copyToClipboard(result.value.formattedDsl)
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

}