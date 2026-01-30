package org.treeroot.devlog.page.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TooltipEllipsisText(
    fullText: String,
    maxWidth: Dp,
    color: Color,
    fontSize: TextUnit = MaterialTheme.typography.bodyMedium.fontSize,
    fontFamily: FontFamily = FontFamily.Monospace,
    modifier: Modifier = Modifier
) {
    var showTooltip by remember { mutableStateOf(false) }
    var textOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .onPointerEvent(PointerEventType.Enter, onEvent = {
                showTooltip = true
            })
            .onPointerEvent(PointerEventType.Exit, onEvent = {
                showTooltip = false
            })
            .onGloballyPositioned { coordinates ->
                textOffset = coordinates.positionInRoot()
            }
    ) {
        Text(
            text = fullText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = color,
            fontSize = fontSize,
            fontFamily = fontFamily,
            modifier = Modifier.width(maxWidth)
        )
    }

    if (showTooltip && fullText.isNotEmpty()) {
        Popup(
            offset = IntOffset(
                x = textOffset.x.toInt(),
                // 显示在上方
                y = (textOffset.y - 30).toInt()
            ),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.small
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(6.dp)
            ) {
                Text(
                    text = fullText,
                    modifier = Modifier.padding(6.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}