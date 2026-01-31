package org.treeroot.devlog.page.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.treeroot.devlog.page.enums.MessageType

/**
 * 消息类型枚举
 */


/**
 * 消息显示持续时间
 */
data class MessageDuration(
    val durationMillis: Int
) {
    companion object {
        val Short = MessageDuration(2000)  // 2秒
        val Medium = MessageDuration(4000) // 4秒
        val Long = MessageDuration(6000)   // 6秒
        val Infinite = MessageDuration(-1) // 无限期显示（直到手动关闭）
    }
}

/**
 * 无需确认的Message组件
 *
 * @param message 消息内容
 * @param messageType 消息类型（INFO, WARNING, ERROR, SUCCESS）
 * @param isVisible 控制消息是否可见
 * @param duration 消息显示持续时间
 * @param onClose 消息关闭回调
 * @param modifier 修饰符
 * @param showCloseButton 是否显示关闭按钮
 * @param backgroundColor 背景颜色
 * @param textColor 文字颜色
 * @param elevation 阴影深度
 * @param cornerRadius 圆角大小
 */
@Composable
fun Message(
    message: String,
    messageType: MessageType,
    isVisible: Boolean,
    duration: MessageDuration = MessageDuration.Medium,
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = true,
    backgroundColor: Color? = null,
    textColor: Color? = null,
    elevation: Dp = 4.dp,
    cornerRadius: Dp = 8.dp
) {
    val scope = rememberCoroutineScope()

    // 根据消息类型获取默认颜色
    val defaultBackgroundColor = when (messageType) {
        MessageType.INFO -> MaterialTheme.colorScheme.primaryContainer
        MessageType.WARNING -> MaterialTheme.colorScheme.secondaryContainer
        MessageType.ERROR -> MaterialTheme.colorScheme.errorContainer
        MessageType.SUCCESS -> MaterialTheme.colorScheme.tertiaryContainer
    }

    val defaultTextColor = when (messageType) {
        MessageType.INFO -> MaterialTheme.colorScheme.onPrimaryContainer
        MessageType.WARNING -> MaterialTheme.colorScheme.onSecondaryContainer
        MessageType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        MessageType.SUCCESS -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    val actualBackgroundColor = backgroundColor ?: defaultBackgroundColor
    val actualTextColor = textColor ?: defaultTextColor

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(durationMillis = 300)),
        exit = fadeOut(animationSpec = tween(durationMillis = 300))
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = actualBackgroundColor
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            shape = RoundedCornerShape(cornerRadius)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    color = actualTextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                if (showCloseButton) {
                    CloseButton(onClose)
                }
            }
        }
    }

    // 处理自动消失逻辑
    LaunchedEffect(isVisible, duration) {
        if (isVisible && duration.durationMillis > 0) {
            delay(duration.durationMillis.toLong())
            onClose()
        }
    }
}

/**
 * 使用状态管理的消息组件
 *
 * @param message 消息内容
 * @param messageType 消息类型
 * @param duration 消息显示持续时间
 * @param modifier 修饰符
 * @param showCloseButton 是否显示关闭按钮
 * @param backgroundColor 背景颜色
 * @param textColor 文字颜色
 * @param elevation 阴影深度
 * @param cornerRadius 圆角大小
 */
@Composable
fun ManagedMessage(
    message: String,
    messageType: MessageType = MessageType.INFO,
    duration: MessageDuration = MessageDuration.Medium,
    modifier: Modifier = Modifier,
    showCloseButton: Boolean = true,
    backgroundColor: Color? = null,
    textColor: Color? = null,
    elevation: Dp = 4.dp,
    cornerRadius: Dp = 8.dp
) {
    var isVisible by remember { mutableStateOf(true) }

    Message(
        message = message,
        messageType = messageType,
        isVisible = isVisible,
        duration = duration,
        onClose = { isVisible = false },
        modifier = modifier,
        showCloseButton = showCloseButton,
        backgroundColor = backgroundColor,
        textColor = textColor,
        elevation = elevation,
        cornerRadius = cornerRadius
    )
}

/**
 * 消息管理器，用于控制多个消息的显示
 */
class MessageManager {
    private val _messages = mutableListOf<MessageData>()
    val messages: List<MessageData> = _messages

    fun showMessage(
        message: String,
        type: MessageType = MessageType.INFO,
        duration: MessageDuration = MessageDuration.Medium
    ) {
        val msgData = MessageData(
            id = System.currentTimeMillis(), // 使用时间戳作为唯一ID
            message = message,
            type = type,
            duration = duration
        )
        _messages.add(msgData)
    }

    fun removeMessage(id: Long) {
        _messages.removeAll { it.id == id }
    }

    fun clearAllMessages() {
        _messages.clear()
    }
}

/**
 * 消息数据类
 */
data class MessageData(
    val id: Long,
    val message: String,
    val type: MessageType,
    val duration: MessageDuration
)

/**
 * 使用消息管理器的消息列表组件
 *
 * @param messageManager 消息管理器实例
 * @param modifier 修饰符
 */
@Composable
fun MessageList(
    messageManager: MessageManager,
    modifier: Modifier = Modifier
) {
    val messages by rememberUpdatedState(messageManager.messages)

    Column(modifier = modifier) {
        messages.forEach { msg ->
            var isVisible by remember { mutableStateOf(true) }

            if (isVisible) {
                Message(
                    message = msg.message,
                    messageType = msg.type,
                    isVisible = true,
                    duration = msg.duration,
                    onClose = {
                        isVisible = false
                        messageManager.removeMessage(msg.id)
                    }
                )
            }
        }
    }
}