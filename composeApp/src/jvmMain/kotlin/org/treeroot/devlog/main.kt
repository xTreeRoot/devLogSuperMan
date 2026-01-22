package org.treeroot.devlog

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DevLog_SuperMan",
        state = WindowState(
            size = DpSize(width = 1000.dp, height = 800.dp), // 初始宽高
            position = WindowPosition.Aligned(Alignment.Center) // 居中
        )
    ) {
        App()
    }
}