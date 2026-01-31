package org.treeroot.devlog.business.view

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.treeroot.devlog.business.EsDslFormatterService
import org.treeroot.devlog.business.model.EsDslResult
import org.treeroot.devlog.util.ClipboardHelper

/**
 * ES DSL处理器的ViewModel
 * 管理ES DSL相关的UI状态和业务逻辑
 */
class EsDslViewModel : ErrorCallbackHandler {

    private val esDslService = EsDslFormatterService()

    // UI状态
    private val _originalDsl = mutableStateOf("")
    val originalDsl: State<String> = _originalDsl

    private val _result = mutableStateOf(EsDslResult(success = true))
    val result: State<EsDslResult> = _result

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // 错误处理相关状态
    private val _onErrorCallback = mutableStateOf<((String) -> Unit)?>(null)


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
     * 格式化ES DSL
     */
    fun formatDsl() {
        if (_isLoading.value) return

        CoroutineScope(Dispatchers.Default).launch {
            _isLoading.value = true
            try {
                _result.value = esDslService.separateDslAndResponse(_originalDsl.value)
            } catch (e: Exception) {
                // 通过错误回调通知UI
                _onErrorCallback.value?.invoke("格式化ES DSL时发生错误: ${e.message}")
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

    /**
     * 设置错误回调函数
     */
    override fun setErrorCallback(errorCallback: (String) -> Unit) {
        _onErrorCallback.value = errorCallback
    }

    /**
     * 清除错误回调函数
     */
    override fun clearErrorCallback() {
        _onErrorCallback.value = null
    }
}