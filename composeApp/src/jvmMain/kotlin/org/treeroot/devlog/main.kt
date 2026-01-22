package org.treeroot.devlog

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DevLog_SuperMan",
    ) {
        App()
    }
}