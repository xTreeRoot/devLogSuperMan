package org.treeroot.devlog

import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    // 初始化数据库

    Window(
        onCloseRequest = ::exitApplication,
        title = "devLogSuperMan",
        state = WindowState(
            // 初始宽高
            size = DpSize(width = 1400.dp, height = 800.dp),
            // 居中
            position = WindowPosition.Aligned(Alignment.Center)
        )
    ) {
        App()
    }
}