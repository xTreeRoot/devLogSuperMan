package org.treeroot.devlog.page.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.DialogProperties
import org.treeroot.devlog.page.enums.MessageType

// 颜色样式定义（ImageVector 由你自行传入）
data class MessageStyle(
    val titleColor: Color,
    val textColor: Color,
    val containerColor: Color
)

@Composable
fun rememberMessageStyle(messageType: MessageType): MessageStyle {
    return when (messageType) {
        MessageType.INFO -> MessageStyle(
            titleColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onSurface,
            containerColor = AlertDialogDefaults.containerColor
        )
        MessageType.WARNING -> MessageStyle(
            titleColor = MaterialTheme.colorScheme.onSurfaceVariant,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            containerColor = AlertDialogDefaults.containerColor
        )
        MessageType.ERROR -> MessageStyle(
            titleColor = MaterialTheme.colorScheme.error,
            textColor = MaterialTheme.colorScheme.error,
            containerColor = AlertDialogDefaults.containerColor
        )
        MessageType.SUCCESS -> MessageStyle(
            titleColor = MaterialTheme.colorScheme.primary,
            textColor = MaterialTheme.colorScheme.onSurface,
            containerColor = AlertDialogDefaults.containerColor
        )
    }
}

@Composable
fun MessageDialog(
    messageType: MessageType,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    confirmText: String = "确定"
) {
    val style = rememberMessageStyle(messageType)

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        icon = {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = style.titleColor
                )
            }
        },
        title = {
            Text(title, color = style.titleColor)
        },
        text = {
            Text(message, color = style.textColor)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(confirmText)
            }
        },
        containerColor = style.containerColor,
        properties = DialogProperties(
            dismissOnClickOutside = false
        )
    )
}