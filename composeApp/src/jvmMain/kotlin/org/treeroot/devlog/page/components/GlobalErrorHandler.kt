package org.treeroot.devlog.page.components

import androidx.compose.runtime.*
import org.treeroot.devlog.business.view.ErrorCallbackHandler
import org.treeroot.devlog.page.enums.MessageType

/**
 * 全局错误处理器组件
 * 用于统一处理来自不同ViewModel的错误回调，并显示错误对话框
 */
@Composable
fun GlobalErrorHandler(
    vararg errorHandlers: ErrorCallbackHandler,
    onErrorHandled: () -> Unit = {}
) {
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // 设置所有错误处理器的回调
    LaunchedEffect(Unit) {
        errorHandlers.forEach { handler ->
            handler.setErrorCallback { msg ->
                errorMessage = msg
                showErrorDialog = true
            }
        }
    }

    // 清理所有错误处理器的回调
    DisposableEffect(Unit) {
        onDispose {
            errorHandlers.forEach { handler ->
                handler.clearErrorCallback()
            }
        }
    }

    // 错误对话框
    if (showErrorDialog) {
        MessageDialog(
            messageType = MessageType.ERROR,
            title = "错误",
            message = errorMessage,
            onDismiss = {
                showErrorDialog = false
                onErrorHandled()
            },
            confirmText = "确定"
        )
    }
}