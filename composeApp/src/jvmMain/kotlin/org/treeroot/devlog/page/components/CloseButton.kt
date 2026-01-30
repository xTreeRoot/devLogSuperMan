package org.treeroot.devlog.page.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun CloseButton(onDismissRequest: () -> Unit) {
    IconButton(onClick = onDismissRequest) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val strokeWidth = 3f
            // 左上 -> 右下
            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(size.width, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
            // 右上 -> 左下
            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(size.width, 0f),
                end = androidx.compose.ui.geometry.Offset(0f, size.height),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}