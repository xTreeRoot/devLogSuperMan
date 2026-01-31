package org.treeroot.devlog.page.components

import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.DialogProperties

/**
 * 通用消息对话框类型枚举
 */
enum class MessageType {
    INFO, WARNING, ERROR
}

data class MessageStyle(
    val iconRes: ImageVector?,
    val containerColor: Color,
    val titleColor: Color,
    val textColor: Color
)


/**
 * 通用消息对话框
 *
 * @param messageType 消息类型（INFO, WARNING, ERROR）
 * @param title 对话框标题
 * @param message 显示的消息内容
 * @param onConfirm 确认按钮回调
 * @param onDismiss 取消/关闭对话框回调
 * @param confirmButtonText 确认按钮文字
 * @param dismissButtonText 取消按钮文字
 * @param modifier 修饰符
 * @param showDismissButton 是否显示取消按钮
 */

@Composable
fun MessageDialog(
    messageType: MessageType,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmButtonText: String = "确定",
    dismissButtonText: String = "取消",
    modifier: Modifier = Modifier,
    showDismissButton: Boolean = true,
    icon: (@Composable () -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    val (iconRes, containerColor, titleColor, textColor) = when (messageType) {
        MessageType.INFO -> MessageStyle(
            null,
            AlertDialogDefaults.containerColor,
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.onSurface
        )

        MessageType.WARNING -> MessageStyle(
            null,
            AlertDialogDefaults.containerColor,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )

        MessageType.ERROR -> MessageStyle(
            null,
            AlertDialogDefaults.containerColor,
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.error
        )
    }

    /**
     * 信息提示对话框
     *
     * @param title 对话框标题
     * @param message 显示的消息内容
     * @param onConfirm 确认按钮回调
     * @param onDismiss 取消/关闭对话框回调
     * @param confirmButtonText 确认按钮文字
     * @param dismissButtonText 取消按钮文字
     * @param modifier 修饰符
     * @param showDismissButton 是否显示取消按钮
     */
    @Composable
    fun InfoDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
        confirmButtonText: String = "确定",
        dismissButtonText: String = "取消",
        modifier: Modifier = Modifier,
        showDismissButton: Boolean = true,
        icon: (@Composable () -> Unit)? = null,
        properties: DialogProperties = DialogProperties()
    ) {
        MessageDialog(
            messageType = MessageType.INFO,
            title = title,
            message = message,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            confirmButtonText = confirmButtonText,
            dismissButtonText = dismissButtonText,
            modifier = modifier,
            showDismissButton = showDismissButton,
            icon = icon,
            properties = properties
        )
    }

    /**
     * 警告对话框
     *
     * @param title 对话框标题
     * @param message 显示的消息内容
     * @param onConfirm 确认按钮回调
     * @param onDismiss 取消/关闭对话框回调
     * @param confirmButtonText 确认按钮文字
     * @param dismissButtonText 取消按钮文字
     * @param modifier 修饰符
     * @param showDismissButton 是否显示取消按钮
     */
    @Composable
    fun WarningDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
        confirmButtonText: String = "确定",
        dismissButtonText: String = "取消",
        modifier: Modifier = Modifier,
        showDismissButton: Boolean = true,
        icon: (@Composable () -> Unit)? = null,
        properties: DialogProperties = DialogProperties()
    ) {
        MessageDialog(
            messageType = MessageType.WARNING,
            title = title,
            message = message,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            confirmButtonText = confirmButtonText,
            dismissButtonText = dismissButtonText,
            modifier = modifier,
            showDismissButton = showDismissButton,
            icon = icon,
            properties = properties
        )
    }

    /**
     * 错误对话框
     *
     * @param title 对话框标题
     * @param message 显示的消息内容
     * @param onConfirm 确认按钮回调
     * @param onDismiss 取消/关闭对话框回调
     * @param confirmButtonText 确认按钮文字
     * @param dismissButtonText 取消按钮文字
     * @param modifier 修饰符
     * @param showDismissButton 是否显示取消按钮
     */
    @Composable
    fun ErrorDialog(
        title: String,
        message: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit,
        confirmButtonText: String = "确定",
        dismissButtonText: String = "取消",
        modifier: Modifier = Modifier,
        showDismissButton: Boolean = true,
        icon: (@Composable () -> Unit)? = null,
        properties: DialogProperties = DialogProperties()
    ) {
        MessageDialog(
            messageType = MessageType.ERROR,
            title = title,
            message = message,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            confirmButtonText = confirmButtonText,
            dismissButtonText = dismissButtonText,
            modifier = modifier,
            showDismissButton = showDismissButton,
            icon = icon,
            properties = properties
        )
    }
}